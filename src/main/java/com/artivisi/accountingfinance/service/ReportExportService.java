package com.artivisi.accountingfinance.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportExportService {

    private static final String COMPANY_NAME = "PT ArtiVisi Intermedia";
    private static final DecimalFormat NUMBER_FORMAT;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("id", "ID"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        NUMBER_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    // PDF Fonts
    private Font getTitleFont() {
        return new Font(Font.HELVETICA, 14, Font.BOLD);
    }

    private Font getSubtitleFont() {
        return new Font(Font.HELVETICA, 10, Font.NORMAL);
    }

    private Font getHeaderFont() {
        return new Font(Font.HELVETICA, 9, Font.BOLD);
    }

    private Font getNormalFont() {
        return new Font(Font.HELVETICA, 9, Font.NORMAL);
    }

    private Font getBoldFont() {
        return new Font(Font.HELVETICA, 9, Font.BOLD);
    }

    // ==================== TRIAL BALANCE ====================

    public byte[] exportTrialBalanceToPdf(ReportService.TrialBalanceReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            addReportHeader(document, "NERACA SALDO", "Trial Balance",
                    "Per tanggal " + report.asOfDate().format(DATE_FORMAT));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{10, 40, 25, 25});
            table.setSpacingBefore(20);

            addTableHeader(table, "Kode", "Nama Akun", "Debit", "Kredit");

            for (ReportService.TrialBalanceItem item : report.items()) {
                addTableCell(table, item.account().getAccountCode(), Element.ALIGN_LEFT);
                addTableCell(table, item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, formatNumber(item.debitBalance()), Element.ALIGN_RIGHT);
                addTableCell(table, formatNumber(item.creditBalance()), Element.ALIGN_RIGHT);
            }

            addTotalRow(table, "TOTAL", formatNumber(report.totalDebit()), formatNumber(report.totalCredit()));

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("Error generating Trial Balance PDF", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    public byte[] exportTrialBalanceToExcel(ReportService.TrialBalanceReport report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Neraca Saldo");
            int rowNum = 0;

            rowNum = addExcelHeader(workbook, sheet, rowNum, "NERACA SALDO",
                    "Per tanggal " + report.asOfDate().format(DATE_FORMAT), 4);

            Row headerRow = sheet.createRow(rowNum++);
            CellStyle headerStyle = createHeaderStyle(workbook);
            createCell(headerRow, 0, "Kode", headerStyle);
            createCell(headerRow, 1, "Nama Akun", headerStyle);
            createCell(headerRow, 2, "Debit", headerStyle);
            createCell(headerRow, 3, "Kredit", headerStyle);

            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);

            for (ReportService.TrialBalanceItem item : report.items()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, item.account().getAccountCode(), textStyle);
                createCell(row, 1, item.account().getAccountName(), textStyle);
                createNumericCell(row, 2, item.debitBalance(), numberStyle);
                createNumericCell(row, 3, item.creditBalance(), numberStyle);
            }

            Row totalRow = sheet.createRow(rowNum);
            CellStyle totalStyle = createTotalStyle(workbook);
            createCell(totalRow, 0, "", totalStyle);
            createCell(totalRow, 1, "TOTAL", totalStyle);
            createNumericCell(totalRow, 2, report.totalDebit(), totalStyle);
            createNumericCell(totalRow, 3, report.totalCredit(), totalStyle);

            autoSizeColumns(sheet, 4);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Trial Balance Excel", e);
            throw new RuntimeException("Failed to generate Excel: " + e.getMessage());
        }
    }

    // ==================== BALANCE SHEET ====================

    public byte[] exportBalanceSheetToPdf(ReportService.BalanceSheetReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            addReportHeader(document, "LAPORAN POSISI KEUANGAN", "Balance Sheet",
                    "Per tanggal " + report.asOfDate().format(DATE_FORMAT));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{70, 30});
            table.setSpacingBefore(20);

            // ASSETS
            addSectionHeader(table, "ASET");
            for (ReportService.BalanceSheetItem item : report.assetItems()) {
                addTableCell(table, "  " + item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, formatNumber(item.balance()), Element.ALIGN_RIGHT);
            }
            addSubtotalRow(table, "Total Aset", formatNumber(report.totalAssets()));

            // LIABILITIES
            addSectionHeader(table, "LIABILITAS");
            for (ReportService.BalanceSheetItem item : report.liabilityItems()) {
                addTableCell(table, "  " + item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, formatNumber(item.balance()), Element.ALIGN_RIGHT);
            }
            addSubtotalRow(table, "Total Liabilitas", formatNumber(report.totalLiabilities()));

            // EQUITY
            addSectionHeader(table, "EKUITAS");
            for (ReportService.BalanceSheetItem item : report.equityItems()) {
                addTableCell(table, "  " + item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, formatNumber(item.balance()), Element.ALIGN_RIGHT);
            }
            addTableCell(table, "  Laba Tahun Berjalan", Element.ALIGN_LEFT);
            addTableCell(table, formatNumber(report.currentYearEarnings()), Element.ALIGN_RIGHT);
            addSubtotalRow(table, "Total Ekuitas", formatNumber(report.totalEquity()));

            // TOTAL LIABILITIES + EQUITY
            BigDecimal totalLiabilitiesAndEquity = report.totalLiabilities().add(report.totalEquity());
            addTotalRow(table, "TOTAL LIABILITAS + EKUITAS", formatNumber(totalLiabilitiesAndEquity), null);

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("Error generating Balance Sheet PDF", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    public byte[] exportBalanceSheetToExcel(ReportService.BalanceSheetReport report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Laporan Posisi Keuangan");
            int rowNum = 0;

            rowNum = addExcelHeader(workbook, sheet, rowNum, "LAPORAN POSISI KEUANGAN",
                    "Per tanggal " + report.asOfDate().format(DATE_FORMAT), 2);

            CellStyle sectionStyle = createSectionStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);

            // ASSETS
            Row assetHeader = sheet.createRow(rowNum++);
            createCell(assetHeader, 0, "ASET", sectionStyle);
            for (ReportService.BalanceSheetItem item : report.assetItems()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, "  " + item.account().getAccountName(), textStyle);
                createNumericCell(row, 1, item.balance(), numberStyle);
            }
            Row assetTotal = sheet.createRow(rowNum++);
            createCell(assetTotal, 0, "Total Aset", totalStyle);
            createNumericCell(assetTotal, 1, report.totalAssets(), totalStyle);
            rowNum++;

            // LIABILITIES
            Row liabilityHeader = sheet.createRow(rowNum++);
            createCell(liabilityHeader, 0, "LIABILITAS", sectionStyle);
            for (ReportService.BalanceSheetItem item : report.liabilityItems()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, "  " + item.account().getAccountName(), textStyle);
                createNumericCell(row, 1, item.balance(), numberStyle);
            }
            Row liabilityTotal = sheet.createRow(rowNum++);
            createCell(liabilityTotal, 0, "Total Liabilitas", totalStyle);
            createNumericCell(liabilityTotal, 1, report.totalLiabilities(), totalStyle);
            rowNum++;

            // EQUITY
            Row equityHeader = sheet.createRow(rowNum++);
            createCell(equityHeader, 0, "EKUITAS", sectionStyle);
            for (ReportService.BalanceSheetItem item : report.equityItems()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, "  " + item.account().getAccountName(), textStyle);
                createNumericCell(row, 1, item.balance(), numberStyle);
            }
            Row earningsRow = sheet.createRow(rowNum++);
            createCell(earningsRow, 0, "  Laba Tahun Berjalan", textStyle);
            createNumericCell(earningsRow, 1, report.currentYearEarnings(), numberStyle);
            Row equityTotal = sheet.createRow(rowNum++);
            createCell(equityTotal, 0, "Total Ekuitas", totalStyle);
            createNumericCell(equityTotal, 1, report.totalEquity(), totalStyle);
            rowNum++;

            // TOTAL
            Row grandTotal = sheet.createRow(rowNum);
            createCell(grandTotal, 0, "TOTAL LIABILITAS + EKUITAS", totalStyle);
            createNumericCell(grandTotal, 1, report.totalLiabilities().add(report.totalEquity()), totalStyle);

            autoSizeColumns(sheet, 2);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Balance Sheet Excel", e);
            throw new RuntimeException("Failed to generate Excel: " + e.getMessage());
        }
    }

    // ==================== INCOME STATEMENT ====================

    public byte[] exportIncomeStatementToPdf(ReportService.IncomeStatementReport report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            addReportHeader(document, "LAPORAN LABA RUGI", "Income Statement",
                    "Periode " + report.startDate().format(DATE_FORMAT) + " - " + report.endDate().format(DATE_FORMAT));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{70, 30});
            table.setSpacingBefore(20);

            // REVENUE
            addSectionHeader(table, "PENDAPATAN");
            for (ReportService.IncomeStatementItem item : report.revenueItems()) {
                addTableCell(table, "  " + item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, formatNumber(item.balance()), Element.ALIGN_RIGHT);
            }
            addSubtotalRow(table, "Total Pendapatan", formatNumber(report.totalRevenue()));

            // EXPENSES
            addSectionHeader(table, "BEBAN OPERASIONAL");
            for (ReportService.IncomeStatementItem item : report.expenseItems()) {
                addTableCell(table, "  " + item.account().getAccountName(), Element.ALIGN_LEFT);
                addTableCell(table, "(" + formatNumber(item.balance()) + ")", Element.ALIGN_RIGHT);
            }
            addSubtotalRow(table, "Total Beban", "(" + formatNumber(report.totalExpense()) + ")");

            // NET INCOME
            String netIncomeLabel = report.netIncome().compareTo(BigDecimal.ZERO) >= 0 ? "LABA BERSIH" : "RUGI BERSIH";
            addTotalRow(table, netIncomeLabel, formatNumber(report.netIncome()), null);

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (DocumentException | IOException e) {
            log.error("Error generating Income Statement PDF", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    public byte[] exportIncomeStatementToExcel(ReportService.IncomeStatementReport report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Laporan Laba Rugi");
            int rowNum = 0;

            rowNum = addExcelHeader(workbook, sheet, rowNum, "LAPORAN LABA RUGI",
                    "Periode " + report.startDate().format(DATE_FORMAT) + " - " + report.endDate().format(DATE_FORMAT), 2);

            CellStyle sectionStyle = createSectionStyle(workbook);
            CellStyle textStyle = createTextStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);

            // REVENUE
            Row revenueHeader = sheet.createRow(rowNum++);
            createCell(revenueHeader, 0, "PENDAPATAN", sectionStyle);
            for (ReportService.IncomeStatementItem item : report.revenueItems()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, "  " + item.account().getAccountName(), textStyle);
                createNumericCell(row, 1, item.balance(), numberStyle);
            }
            Row revenueTotal = sheet.createRow(rowNum++);
            createCell(revenueTotal, 0, "Total Pendapatan", totalStyle);
            createNumericCell(revenueTotal, 1, report.totalRevenue(), totalStyle);
            rowNum++;

            // EXPENSES
            Row expenseHeader = sheet.createRow(rowNum++);
            createCell(expenseHeader, 0, "BEBAN OPERASIONAL", sectionStyle);
            for (ReportService.IncomeStatementItem item : report.expenseItems()) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, "  " + item.account().getAccountName(), textStyle);
                createNumericCell(row, 1, item.balance().negate(), numberStyle);
            }
            Row expenseTotal = sheet.createRow(rowNum++);
            createCell(expenseTotal, 0, "Total Beban", totalStyle);
            createNumericCell(expenseTotal, 1, report.totalExpense().negate(), totalStyle);
            rowNum++;

            // NET INCOME
            String netIncomeLabel = report.netIncome().compareTo(BigDecimal.ZERO) >= 0 ? "LABA BERSIH" : "RUGI BERSIH";
            Row netIncomeRow = sheet.createRow(rowNum);
            createCell(netIncomeRow, 0, netIncomeLabel, totalStyle);
            createNumericCell(netIncomeRow, 1, report.netIncome(), totalStyle);

            autoSizeColumns(sheet, 2);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error generating Income Statement Excel", e);
            throw new RuntimeException("Failed to generate Excel: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    private String formatNumber(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            return "-";
        }
        return NUMBER_FORMAT.format(value);
    }

    // PDF Helpers

    private void addReportHeader(Document document, String title, String subtitle, String period)
            throws DocumentException {
        Paragraph companyPara = new Paragraph(COMPANY_NAME, getTitleFont());
        companyPara.setAlignment(Element.ALIGN_CENTER);
        document.add(companyPara);

        Paragraph titlePara = new Paragraph(title, getTitleFont());
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingBefore(10);
        document.add(titlePara);

        Paragraph subtitlePara = new Paragraph(subtitle, getSubtitleFont());
        subtitlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitlePara);

        Paragraph periodPara = new Paragraph(period, getSubtitleFont());
        periodPara.setAlignment(Element.ALIGN_CENTER);
        periodPara.setSpacingAfter(10);
        document.add(periodPara);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, getHeaderFont()));
            cell.setBackgroundColor(new Color(240, 240, 240));
            cell.setPadding(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getNormalFont()));
        cell.setPadding(4);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    private void addSectionHeader(PdfPTable table, String title) {
        PdfPCell cell = new PdfPCell(new Phrase(title, getBoldFont()));
        cell.setColspan(2);
        cell.setPadding(6);
        cell.setBackgroundColor(new Color(245, 245, 245));
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    private void addSubtotalRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, getBoldFont()));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(new Color(250, 250, 250));
        labelCell.setBorderWidth(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, getBoldFont()));
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setBackgroundColor(new Color(250, 250, 250));
        valueCell.setBorderWidth(0.5f);
        table.addCell(valueCell);
    }

    private void addTotalRow(PdfPTable table, String label, String value1, String value2) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, getBoldFont()));
        labelCell.setPadding(6);
        labelCell.setBackgroundColor(new Color(230, 230, 230));
        labelCell.setBorderWidth(1f);

        if (value2 == null) {
            labelCell.setColspan(1);
            table.addCell(labelCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(value1, getBoldFont()));
            valueCell.setPadding(6);
            valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            valueCell.setBackgroundColor(new Color(230, 230, 230));
            valueCell.setBorderWidth(1f);
            table.addCell(valueCell);
        } else {
            table.addCell(labelCell);

            PdfPCell debitCell = new PdfPCell(new Phrase(value1, getBoldFont()));
            debitCell.setPadding(6);
            debitCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            debitCell.setBackgroundColor(new Color(230, 230, 230));
            debitCell.setBorderWidth(1f);
            table.addCell(debitCell);

            PdfPCell creditCell = new PdfPCell(new Phrase(value2, getBoldFont()));
            creditCell.setPadding(6);
            creditCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            creditCell.setBackgroundColor(new Color(230, 230, 230));
            creditCell.setBorderWidth(1f);
            table.addCell(creditCell);
        }
    }

    // Excel Helpers

    private int addExcelHeader(Workbook workbook, Sheet sheet, int startRow, String title, String period, int columns) {
        CellStyle titleStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        Row companyRow = sheet.createRow(startRow++);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue(COMPANY_NAME);
        companyCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, columns - 1));

        Row titleRow = sheet.createRow(startRow++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, columns - 1));

        CellStyle subtitleStyle = workbook.createCellStyle();
        subtitleStyle.setAlignment(HorizontalAlignment.CENTER);
        Row periodRow = sheet.createRow(startRow++);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue(period);
        periodCell.setCellStyle(subtitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow - 1, startRow - 1, 0, columns - 1));

        startRow++; // Empty row
        return startRow;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createSectionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
