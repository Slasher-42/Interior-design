package com.ced.User.Client.Service.repository;

import com.ced.User.Client.Service.domain.ClientInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientInteractionRepository extends JpaRepository<ClientInteraction, UUID> {
    Page<ClientInteraction> findByClientIdOrderByOccurredAtDesc(UUID clientId, Pageable pageable);
}
