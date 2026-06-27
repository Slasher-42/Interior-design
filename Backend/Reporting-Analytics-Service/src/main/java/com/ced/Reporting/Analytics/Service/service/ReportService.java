package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.domain.ClientRecord;
import com.ced.Reporting.Analytics.Service.domain.FeedbackRecord;
import com.ced.Reporting.Analytics.Service.domain.ProjectRecord;
import com.ced.Reporting.Analytics.Service.domain.PurchaseOrderRecord;
import com.ced.Reporting.Analytics.Service.domain.QuotationRecord;
import com.ced.Reporting.Analytics.Service.domain.ReportDimension;
import com.ced.Reporting.Analytics.Service.domain.ServiceRequestRecord;
import com.ced.Reporting.Analytics.Service.domain.UserSignup;
import com.ced.Reporting.Analytics.Service.dto.ReportResult;
import com.ced.Reporting.Analytics.Service.repository.ClientRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.FeedbackRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.ProjectRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.PurchaseOrderRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.QuotationRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.ServiceRequestRecordRepository;
import com.ced.Reporting.Analytics.Service.repository.UserSignupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ServiceRequestRecordRepository serviceRequestRecordRepository;
    private final QuotationRecordRepository quotationRecordRepository;
    private final ProjectRecordRepository projectRecordRepository;
    private final PurchaseOrderRecordRepository purchaseOrderRecordRepository;
    private final FeedbackRecordRepository feedbackRecordRepository;
    private final UserSignupRepository userSignupRepository;
    private final ClientRecordRepository clientRecordRepository;

    public ReportResult generate(ReportDimension dimension, Instant from, Instant to) {
        return switch (dimension) {
            case SERVICE_REQUESTS -> serviceRequests(from, to);
            case QUOTATIONS -> quotations(from, to);
            case PROJECTS -> projects(from, to);
            case PROCUREMENT -> procurement(from, to);
            case FEEDBACK -> feedback(from, to);
            case USER_SIGNUPS -> userSignups(from, to);
            case CLIENTS -> clients(from, to);
        };
    }

    private ReportResult serviceRequests(Instant from, Instant to) {
        List<ServiceRequestRecord> records = serviceRequestRecordRepository.findAll().stream()
                .filter(r -> inRange(r.getCreatedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(r -> List.of(r.getId().toString(), r.getClientId().toString(), r.getCategory(),
                        r.getPriority().name(), r.getCreatedAt().toString()))
                .toList();
        return new ReportResult(List.of("Request ID", "Client ID", "Category", "Priority", "Created At"),
                rows, "Total requests: " + records.size());
    }

    private ReportResult quotations(Instant from, Instant to) {
        List<QuotationRecord> records = quotationRecordRepository.findAll().stream()
                .filter(q -> inRange(q.getCreatedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(q -> List.of(q.getId().toString(), q.getClientId().toString(), q.getTotalAmount().toString(),
                        q.getStatus().name(), q.getCreatedAt().toString()))
                .toList();
        BigDecimal total = records.stream().map(QuotationRecord::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResult(List.of("Quotation ID", "Client ID", "Total Amount", "Status", "Created At"),
                rows, "Total quotations: " + records.size() + ", combined value: " + total);
    }

    private ReportResult projects(Instant from, Instant to) {
        List<ProjectRecord> records = projectRecordRepository.findAll().stream()
                .filter(p -> inRange(p.getCreatedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(p -> List.of(p.getId().toString(), p.getClientId().toString(), p.getStatus().name(),
                        String.valueOf(p.getApprovedBudget()), String.valueOf(p.getFinalCost()), p.getCreatedAt().toString()))
                .toList();
        long active = records.stream().filter(p -> p.getStatus().name().equals("ACTIVE")).count();
        long completed = records.size() - active;
        return new ReportResult(List.of("Project ID", "Client ID", "Status", "Approved Budget", "Final Cost", "Created At"),
                rows, "Active: " + active + ", Completed: " + completed);
    }

    private ReportResult procurement(Instant from, Instant to) {
        List<PurchaseOrderRecord> records = purchaseOrderRecordRepository.findAll().stream()
                .filter(o -> inRange(o.getCreatedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(o -> List.of(o.getId().toString(), o.getProjectId().toString(), o.getVendorId().toString(),
                        o.getEstimatedCost().toString(), o.getCreatedAt().toString()))
                .toList();
        BigDecimal total = records.stream().map(PurchaseOrderRecord::getEstimatedCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ReportResult(List.of("Order ID", "Project ID", "Vendor ID", "Estimated Cost", "Created At"),
                rows, "Total procurement spend: " + total);
    }

    private ReportResult feedback(Instant from, Instant to) {
        List<FeedbackRecord> records = feedbackRecordRepository.findAll().stream()
                .filter(f -> inRange(f.getSubmittedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(f -> List.of(f.getId().toString(), f.getClientId().toString(), f.getProjectId().toString(),
                        String.valueOf(f.getRating()), f.getSubmittedAt().toString()))
                .toList();
        double averageRating = records.stream().mapToInt(FeedbackRecord::getRating).average().orElse(0.0);
        long lowRated = records.stream().filter(FeedbackRecord::isLowRatingFlag).count();
        return new ReportResult(List.of("Feedback ID", "Client ID", "Project ID", "Rating", "Submitted At"),
                rows, String.format("Average rating: %.2f, low-rated: %d", averageRating, lowRated));
    }

    private ReportResult userSignups(Instant from, Instant to) {
        List<UserSignup> records = userSignupRepository.findAll().stream()
                .filter(u -> inRange(u.getRegisteredAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(u -> List.of(u.getId().toString(), u.getRole().name(), u.getRegisteredAt().toString()))
                .toList();
        return new ReportResult(List.of("User ID", "Role", "Registered At"), rows, "Total signups: " + records.size());
    }

    private ReportResult clients(Instant from, Instant to) {
        List<ClientRecord> records = clientRecordRepository.findAll().stream()
                .filter(c -> inRange(c.getCreatedAt(), from, to))
                .toList();
        List<List<String>> rows = records.stream()
                .map(c -> List.of(c.getId().toString(), c.getName(), c.getCreatedAt().toString()))
                .toList();
        return new ReportResult(List.of("Client ID", "Name", "Created At"), rows, "Total clients: " + records.size());
    }

    private boolean inRange(Instant timestamp, Instant from, Instant to) {
        if (timestamp == null) {
            return false;
        }
        return (from == null || !timestamp.isBefore(from)) && (to == null || !timestamp.isAfter(to));
    }
}
