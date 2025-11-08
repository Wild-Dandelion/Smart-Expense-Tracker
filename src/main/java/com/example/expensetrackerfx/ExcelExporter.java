package com.example.expensetrackerfx;

import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static void exportExpenses(ObservableList<ExpenseTrackerFX.Expense> expenses) {
        exportExpenses(expenses, "expenses.xlsx");
    }

    public static void exportExpenses(ObservableList<ExpenseTrackerFX.Expense> expenses, String fileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");

        // 🔹 Common border style for all cells
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setWrapText(true);

        // 🔹 Header style (bold + centered + bordered)
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.MEDIUM);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 🔹 Create header row
        Row header = sheet.createRow(0);
        String[] columns = {"Date", "Category", "Amount", "Description"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 🔹 Add data rows with border style
        int rowNum = 1;
        for (ExpenseTrackerFX.Expense e : expenses) {
            Row row = sheet.createRow(rowNum++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(e.getDate().toString());
            cell0.setCellStyle(borderStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(e.getCategory());
            cell1.setCellStyle(borderStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(e.getAmount());
            cell2.setCellStyle(borderStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(e.getDescription());
            cell3.setCellStyle(borderStyle);
        }

        // 🔹 Auto-size columns for readability
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 🔹 Save the workbook
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("✅ Exported successfully to " + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
