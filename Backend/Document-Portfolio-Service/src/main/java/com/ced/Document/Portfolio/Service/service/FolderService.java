package com.ced.Document.Portfolio.Service.service;

import com.ced.Document.Portfolio.Service.domain.Folder;
import com.ced.Document.Portfolio.Service.dto.CreateFolderRequest;
import com.ced.Document.Portfolio.Service.exception.AppException;
import com.ced.Document.Portfolio.Service.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    @Transactional
    public Folder create(UUID projectId, CreateFolderRequest request) {
        if (request.parentFolderId() != null) {
            Folder parent = getById(request.parentFolderId());
            if (!parent.getProjectId().equals(projectId)) {
                throw new AppException("Parent folder does not belong to this project", HttpStatus.BAD_REQUEST);
            }
        }

        Folder folder = Folder.builder()
                .projectId(projectId)
                .name(request.name())
                .parentFolderId(request.parentFolderId())
                .build();
        return folderRepository.save(folder);
    }

    public Folder getById(UUID id) {
        return folderRepository.findById(id)
                .orElseThrow(() -> new AppException("Folder not found", HttpStatus.NOT_FOUND));
    }

    public List<Folder> listByProject(UUID projectId) {
        return folderRepository.findByProjectId(projectId);
    }
}
