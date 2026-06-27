package com.ced.User.Client.Service.repository;

import com.ced.User.Client.Service.domain.Client;
import com.ced.User.Client.Service.dto.ClientSegmentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {

    Optional<Client> findByUserId(UUID userId);

    @Query("""
            select c.industry as industry, c.country as country, c.city as city, count(c) as total
            from Client c
            group by c.industry, c.country, c.city
            """)
    List<ClientSegmentSummary> segmentSummary();
}
