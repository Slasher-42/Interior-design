package com.ced.Document.Portfolio.Service.service;

import com.ced.Document.Portfolio.Service.domain.Document;
import com.ced.Document.Portfolio.Service.domain.DocumentStatus;
import com.ced.Document.Portfolio.Service.domain.DocumentVersion;
import com.ced.Document.Portfolio.Service.domain.Folder;
import com.ced.Document.Portfolio.Service.domain.Role;
import com.ced.Document.Portfolio.Service.event.DocumentApprovedEvent;
import com.ced.Document.Portfolio.Service.event.KafkaEventPublisher;
import com.ced.Document.Portfolio.Service.exception.AppException;
import com.ced.Document.Portfolio.Service.repository.DocumentRepository;
import com.ced.Document.Portfolio.Service.repository.DocumentVersionRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final FolderService folderService;
    private final DocumentStorageService documentStorageService;
    private final KafkaEventPublisher kafkaEventPublisher;

    public record StoredFile(byte[] bytes, String fileName, String contentType) {
    }

    @Transactional
    public Document upload(UUID projectId, UUID uploaderId, String title, String description,
                            UUID folderId, MultipartFile file) {
        validateFile(file);
        if (folderId != null) {
            Folder folder = folderService.getById(folderId);
            if (!folder.getProjectId().equals(projectId)) {
                throw new AppException("Folder does not belong to this project", HttpStatus.BAD_REQUEST);
            }
        }

        Document document = Document.builder()
                .projectId(projectId)
                .folderId(folderId)
                .title(title)
                .description(description)
                .uploadedBy(uploaderId)
                .build();
        document = documentRepository.save(document);

        saveVersion(document.getId(), 1, uploaderId, file);

        return document;
    }

    @Transactional
    public Document addVersion(UUID documentId, UUID callerId, Role callerRole, MultipartFile file) {
        validateFile(file);
        Document document = getById(documentId);
        if (callerRole != Role.ADMIN && callerRole != Role.PROJECT_MANAGER
                && !document.getUploadedBy().equals(callerId)) {
            throw new AppException("Only the original uploader can add a new version", HttpStatus.FORBIDDEN);
        }

        int nextVersion = document.getCurrentVersion() + 1;
        saveVersion(document.getId(), nextVersion, callerId, file);

        document.setCurrentVersion(nextVersion);
        document.setStatus(DocumentStatus.SUBMITTED);
        document.setApprovedBy(null);
        document.setApprovedAt(null);
        document.setRejectionReason(null);
        return documentRepository.save(document);
    }

    @Transactional
    public Document markUnderReview(UUID id) {
        Document document = getById(id);
        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            throw new AppException("Only submitted documents can be moved to review", HttpStatus.CONFLICT);
        }
        document.setStatus(DocumentStatus.UNDER_REVIEW);
        return documentRepository.save(document);
    }

    @Transactional
    public Document approve(UUID id, UUID approverId) {
        Document document = getById(id);
        if (document.getStatus() != DocumentStatus.SUBMITTED && document.getStatus() != DocumentStatus.UNDER_REVIEW) {
            throw new AppException("Only submitted or under-review documents can be approved", HttpStatus.CONFLICT);
        }
        document.setStatus(DocumentStatus.APPROVED);
        document.setApprovedBy(approverId);
        document.setApprovedAt(Instant.now());
        document.setRejectionReason(null);
        document = documentRepository.save(document);

        kafkaEventPublisher.publishDocumentApproved(DocumentApprovedEvent.builder()
                .documentId(document.getId())
                .projectId(document.getProjectId())
                .uploadedBy(document.getUploadedBy())
                .approvedBy(approverId)
                .approvedAt(document.getApprovedAt())
                .build());

        return document;
    }

    @Transactional
    public Document reject(UUID id, String reason) {
        Document document = getById(id);
        if (document.getStatus() != DocumentStatus.SUBMITTED && document.getStatus() != DocumentStatus.UNDER_REVIEW) {
            throw new AppException("Only submitted or under-review documents can be rejected", HttpStatus.CONFLICT);
        }
        document.setStatus(DocumentStatus.REJECTED);
        document.setRejectionReason(reason);
        return documentRepository.save(document);
    }

    public Document getById(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new AppException("Document not found", HttpStatus.NOT_FOUND));
    }

    public Document getByIdForCaller(UUID id, Role callerRole) {
        Document document = getById(id);
        assertVisible(document, callerRole);
        return document;
    }

    public List<DocumentVersion> listVersions(UUID documentId, Role callerRole) {
        Document document = getByIdForCaller(documentId, callerRole);
        return documentVersionRepository.findByDocumentIdOrderByVersionNumberDesc(document.getId());
    }

    public StoredFile getFile(UUID documentId, Integer versionNumber, Role callerRole) {
        Document document = getByIdForCaller(documentId, callerRole);
        int targetVersion = versionNumber != null ? versionNumber : document.getCurrentVersion();
        DocumentVersion version = documentVersionRepository
                .findByDocumentIdAndVersionNumber(document.getId(), targetVersion)
                .orElseThrow(() -> new AppException("Version not found", HttpStatus.NOT_FOUND));
        byte[] bytes = documentStorageService.read(version.getStoragePath());
        return new StoredFile(bytes, version.getFileName(), version.getContentType());
    }

    public Page<Document> search(UUID projectId, UUID folderId, DocumentStatus status, UUID uploadedBy,
                                  String query, Role callerRole, Pageable pageable) {
        DocumentStatus effectiveStatus = isRestrictedToApproved(callerRole) ? DocumentStatus.APPROVED : status;

        Specification<Document> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("projectId"), projectId));
            if (folderId != null) {
                predicates.add(cb.equal(root.get("folderId"), folderId));
            }
            if (effectiveStatus != null) {
                predicates.add(cb.equal(root.get("status"), effectiveStatus));
            }
            if (uploadedBy != null) {
                predicates.add(cb.equal(root.get("uploadedBy"), uploadedBy));
            }
            if (StringUtils.hasText(query)) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return documentRepository.findAll(spec, pageable);
    }

    private void saveVersion(UUID documentId, int versionNumber, UUID uploaderId, MultipartFile file) {
        String storagePath = documentStorageService.store(documentId, versionNumber, file);
        DocumentVersion version = DocumentVersion.builder()
                .documentId(documentId)
                .versionNumber(versionNumber)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .storagePath(storagePath)
                .uploadedBy(uploaderId)
                .build();
        documentVersionRepository.save(version);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("file is required", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Clients and the sales team are outside the design review loop, so they only ever see
     * approved documents - not submissions still in draft/under-review/rejected states.
     */
    private boolean isRestrictedToApproved(Role callerRole) {
        return callerRole == Role.CLIENT || callerRole == Role.SALES_TEAM;
    }

    private void assertVisible(Document document, Role callerRole) {
        if (isRestrictedToApproved(callerRole) && document.getStatus() != DocumentStatus.APPROVED) {
            throw new AppException("Document not found", HttpStatus.NOT_FOUND);
        }
    }
}
