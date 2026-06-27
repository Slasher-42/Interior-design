package com.ced.Service.Request.Quotation.Service.controller;

import com.ced.Service.Request.Quotation.Service.domain.QuotationStatus;
import com.ced.Service.Request.Quotation.Service.domain.Role;
import com.ced.Service.Request.Quotation.Service.dto.QuotationResponse;
import com.ced.Service.Request.Quotation.Service.dto.RejectQuotationRequest;
import com.ced.Service.Request.Quotation.Service.security.CurrentUser;
import com.ced.Service.Request.Quotation.Service.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @GetMapping
    public ResponseEntity<Page<QuotationResponse>> search(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID requestId,
            @RequestParam(required = false) QuotationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        UUID effectiveClientId = CurrentUser.role() == Role.CLIENT ? CurrentUser.id() : clientId;
        Page<QuotationResponse> result = quotationService
                .search(effectiveClientId, requestId, status, PageRequest.of(page, size))
                .map(QuotationResponse::from);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponse> getById(@PathVariable UUID id) {
        QuotationResponse response = QuotationResponse.from(
                quotationService.getByIdForCaller(id, CurrentUser.id(), CurrentUser.role()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] pdf = quotationService.getPdf(id, CurrentUser.id(), CurrentUser.role());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"quotation-" + id + ".pdf\"")
                .body(pdf);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<QuotationResponse> approve(@PathVariable UUID id) {
        QuotationResponse response = QuotationResponse.from(
                quotationService.approve(id, CurrentUser.id(), CurrentUser.role()));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<QuotationResponse> reject(@PathVariable UUID id,
                                                     @Valid @RequestBody RejectQuotationRequest request) {
        QuotationResponse response = QuotationResponse.from(
                quotationService.reject(id, CurrentUser.id(), CurrentUser.role(), request.reason()));
        return ResponseEntity.ok(response);
    }
}
