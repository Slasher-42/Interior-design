package com.ced.User.Client.Service.service;

import com.ced.User.Client.Service.domain.Client;
import com.ced.User.Client.Service.domain.ClientInteraction;
import com.ced.User.Client.Service.dto.ClientSegmentSummaryResponse;
import com.ced.User.Client.Service.dto.CreateClientRequest;
import com.ced.User.Client.Service.dto.RecordInteractionRequest;
import com.ced.User.Client.Service.event.ClientCreatedEvent;
import com.ced.User.Client.Service.event.KafkaEventPublisher;
import com.ced.User.Client.Service.exception.AppException;
import com.ced.User.Client.Service.repository.ClientInteractionRepository;
import com.ced.User.Client.Service.repository.ClientRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientInteractionRepository clientInteractionRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    @Transactional
    public Client createDirect(CreateClientRequest request) {
        Client client = Client.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .organizationName(request.organizationName())
                .industry(request.industry())
                .country(request.country())
                .city(request.city())
                .website(request.website())
                .build();
        client = clientRepository.save(client);

        kafkaEventPublisher.publishClientCreated(ClientCreatedEvent.builder()
                .clientId(client.getId())
                .name(client.getFullName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .createdAt(client.getCreatedAt())
                .build());

        return client;
    }

    @Transactional
    public Client createLinkedForUser(UUID userId, String fullName, String email) {
        Client client = Client.builder()
                .userId(userId)
                .fullName(fullName)
                .email(email)
                .build();
        return clientRepository.save(client);
    }

    /**
     * Keeps the CRM-facing Client record (used for segmentation) in sync when a CLIENT-role
     * user edits their own organization/industry/location details via their profile page.
     */
    @Transactional
    public void syncLinkedClientDetails(UUID userId, String organizationName, String industry,
                                         String country, String city, String website) {
        clientRepository.findByUserId(userId).ifPresent(client -> {
            if (organizationName != null) client.setOrganizationName(organizationName);
            if (industry != null) client.setIndustry(industry);
            if (country != null) client.setCountry(country);
            if (city != null) client.setCity(city);
            if (website != null) client.setWebsite(website);
            clientRepository.save(client);
        });
    }

    public Client getById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new AppException("Client not found", HttpStatus.NOT_FOUND));
    }

    public Page<Client> search(String industry, String country, String city, String query, Pageable pageable) {
        Specification<Client> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(industry)) {
                predicates.add(cb.equal(cb.lower(root.get("industry")), industry.toLowerCase()));
            }
            if (StringUtils.hasText(country)) {
                predicates.add(cb.equal(cb.lower(root.get("country")), country.toLowerCase()));
            }
            if (StringUtils.hasText(city)) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (StringUtils.hasText(query)) {
                String like = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("organizationName")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return clientRepository.findAll(spec, pageable);
    }

    public List<ClientSegmentSummaryResponse> segmentSummary() {
        return clientRepository.segmentSummary().stream()
                .map(ClientSegmentSummaryResponse::from)
                .toList();
    }

    @Transactional
    public ClientInteraction recordInteraction(UUID clientId, RecordInteractionRequest request) {
        getById(clientId);
        ClientInteraction interaction = ClientInteraction.builder()
                .clientId(clientId)
                .type(request.type())
                .referenceId(request.referenceId())
                .description(request.description())
                .occurredAt(Instant.now())
                .build();
        return clientInteractionRepository.save(interaction);
    }

    public Page<ClientInteraction> listInteractions(UUID clientId, Pageable pageable) {
        return clientInteractionRepository.findByClientIdOrderByOccurredAtDesc(clientId, pageable);
    }
}
