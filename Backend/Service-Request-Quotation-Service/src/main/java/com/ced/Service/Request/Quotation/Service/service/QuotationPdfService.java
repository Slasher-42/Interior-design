package com.ced.Service.Request.Quotation.Service.service;

import com.ced.Service.Request.Quotation.Service.domain.Quotation;
import com.ced.Service.Request.Quotation.Service.domain.ServiceRequest;
import com.ced.Service.Request.Quotation.Service.exception.AppException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Service
public class QuotationPdfService {

    @Value("${app.quotations.storage-dir:./quotations}")
    private String storageDir;

    public String generate(Quotation quotation, ServiceRequest serviceRequest) {
        try {
            Path dir = Path.of(storageDir);
            Files.createDirectories(dir);
            Path file = dir.resolve(quotation.getId() + ".pdf");

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                PDFont titleFont = PDType1Font.HELVETICA_BOLD;
                PDFont bodyFont = PDType1Font.HELVETICA;
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                    y = writeLine(content, titleFont, 18, margin, y, "Interior Design Quotation");
                    y -= 20;

                    String[] lines = {
                            "Quotation ID: " + quotation.getId(),
                            "Service Request ID: " + serviceRequest.getId(),
                            "Category: " + serviceRequest.getCategory(),
                            "Priority: " + serviceRequest.getPriority(),
                            "",
                            "Material Cost: " + quotation.getMaterialCost(),
                            "Labor Cost: " + quotation.getLaborCost(),
                            "Additional Charges: " + quotation.getAdditionalCharges(),
                            "Total Amount: " + quotation.getTotalAmount(),
                            "",
                            "Status: " + quotation.getStatus(),
                            "Generated: " + Instant.now()
                    };

                    for (String line : lines) {
                        y = writeLine(content, bodyFont, 12, margin, y, line);
                    }
                }

                document.save(file.toFile());
            }

            return file.toString();
        } catch (IOException e) {
            throw new AppException("Failed to generate quotation PDF", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public byte[] read(String pdfPath) {
        if (pdfPath == null) {
            throw new AppException("Quotation PDF not available", HttpStatus.NOT_FOUND);
        }
        try {
            return Files.readAllBytes(Path.of(pdfPath));
        } catch (IOException e) {
            throw new AppException("Quotation PDF not found", HttpStatus.NOT_FOUND);
        }
    }

    private float writeLine(PDPageContentStream content, PDFont font, float fontSize,
                             float x, float y, String text) throws IOException {
        if (!text.isEmpty()) {
            content.beginText();
            content.setFont(font, fontSize);
            content.newLineAtOffset(x, y);
            content.showText(text);
            content.endText();
        }
        return y - (fontSize + 6);
    }
}
