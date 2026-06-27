package com.ced.Feedback.Communication.Service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Local read-model populated from project.created events. This service has no synchronous
 * access to Project & Task Service's database, but several downstream events (document.approved,
 * task.completed) reference a project without carrying its clientId/projectManagerId directly -
 * this cache resolves that link the same way Reporting & Analytics builds its own view from the
 * event stream, just scoped to the one lookup this service actually needs.
 */
@Entity
@Table(name = "project_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo {

    @Id
    private UUID projectId;

    @Column(nullable = false)
    private UUID clientId;

    private UUID projectManagerId;
}
