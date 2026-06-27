package com.ced.Reporting.Analytics.Service.service;

import com.ced.Reporting.Analytics.Service.dto.ReportResult;
import com.ced.Reporting.Analytics.Service.exception.AppException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ReportExcelExporter {

    public byte[] export(String title, ReportResult result) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(title);

            Row header = sheet.createRow(0);
            List<String> columns = result.columns();
            for (int i = 0; i < columns.size(); i++) {
                header.createCell(i).setCellValue(columns.get(i));
            }

            int rowIndex = 1;
            for (List<String> rowData : result.rows()) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 0; i < rowData.size(); i++) {
                    row.createCell(i).setCellValue(rowData.get(i));
                }
            }

            Row summaryRow = sheet.createRow(rowIndex + 1);
            summaryRow.createCell(0).setCellValue(result.summary());

            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new AppException("Failed to generate Excel report", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
