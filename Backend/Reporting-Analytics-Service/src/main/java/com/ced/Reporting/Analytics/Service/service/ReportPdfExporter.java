package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.dto.ReportResult;
import com.ced.Reporting.Analytics.Service.exception.AppException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ReportPdfExporter {

    public byte[] export(String title, ReportResult result) {
        try (PDDocument document = new PDDocument()) {
            PDFont titleFont = PDType1Font.HELVETICA_BOLD;
            PDFont bodyFont = PDType1Font.HELVETICA;
            float margin = 50;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = page.getMediaBox().getHeight() - margin;

            y = writeLine(content, titleFont, 16, margin, y, title);
            y -= 10;
            y = writeLine(content, bodyFont, 10, margin, y, result.summary());
            y -= 15;
            y = writeLine(content, titleFont, 10, margin, y, String.join("  |  ", result.columns()));

            for (List<String> row : result.rows()) {
                if (y < margin + 20) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = page.getMediaBox().getHeight() - margin;
                }
                y = writeLine(content, bodyFont, 9, margin, y, String.join("  |  ", row));
            }
            content.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AppException("Failed to generate PDF report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private float writeLine(PDPageContentStream content, PDFont font, float fontSize,
                             float x, float y, String text) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(sanitize(text));
        content.endText();
        return y - (fontSize + 6);
    }

    /**
     * PDFBox's default WinAnsiEncoding can't render arbitrary Unicode, so anything outside
     * basic Latin (e.g. from free-text fields like category names) is replaced rather than
     * letting the encoder throw mid-export.
     */
    private String sanitize(String text) {
        return text == null ? "" : text.replaceAll("[^\\x00-\\x7F]", "?");
    }
}
