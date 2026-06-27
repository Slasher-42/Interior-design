package com.ced.User.Client.Service.service;

import com.ced.User.Client.Service.domain.Role;
import com.ced.User.Client.Service.domain.UserProfile;
import com.ced.User.Client.Service.event.KafkaEventPublisher;
import com.ced.User.Client.Service.event.UserDeletedEvent;
import com.ced.User.Client.Service.exception.AppException;
import com.ced.User.Client.Service.repository.UserProfileRepository;
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
public class UserAdminService {

    private final UserProfileRepository userProfileRepository;
    private final KafkaEventPublisher kafkaEventPublisher;

    public Page<UserProfile> search(Role role, Boolean active, String query, Pageable pageable) {
        Specification<UserProfile> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }
            if (StringUtils.hasText(query)) {
                String like = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("email")), like)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userProfileRepository.findAll(spec, pageable);
    }

    public UserProfile getById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public UserProfile setActive(UUID id, boolean active) {
        UserProfile profile = getById(id);
        profile.setActive(active);
        return userProfileRepository.save(profile);
    }

    @Transactional
    public void delete(UUID id, UUID deletedBy) {
        UserProfile profile = getById(id);
        userProfileRepository.delete(profile);

        kafkaEventPublisher.publishUserDeleted(UserDeletedEvent.builder()
                .userId(id)
                .deletedBy(deletedBy)
                .deletedAt(Instant.now())
                .build());
    }
}
