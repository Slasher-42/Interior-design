package com.ced.Document.Portfolio.Service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentApprovedEvent {
    private UUID documentId;
    private UUID projectId;
    private UUID uploadedBy;
    private UUID approvedBy;
    private Instant approvedAt;
}
