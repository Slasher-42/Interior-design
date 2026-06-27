package com.ced.User.Client.Service.controller;

import com.ced.User.Client.Service.dto.*;
import com.ced.User.Client.Service.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ClientResponse.from(clientService.createDirect(request)));
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponse>> search(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ClientResponse> result = clientService.search(industry, country, city, query, PageRequest.of(page, size))
                .map(ClientResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/segments/summary")
    public ResponseEntity<List<ClientSegmentSummaryResponse>> segmentsSummary() {
        return ResponseEntity.ok(clientService.segmentSummary());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ClientResponse.from(clientService.getById(id)));
    }

    @GetMapping("/{id}/interactions")
    public ResponseEntity<Page<ClientInteractionResponse>> listInteractions(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ClientInteractionResponse> result = clientService.listInteractions(id, PageRequest.of(page, size))
                .map(ClientInteractionResponse::from);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/interactions")
    public ResponseEntity<ClientInteractionResponse> recordInteraction(
            @PathVariable UUID id, @Valid @RequestBody RecordInteractionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClientInteractionResponse.from(clientService.recordInteraction(id, request)));
    }
}
