package com.coder24.effective.excel;

import com.coder24.effective.ChunkSizeEnum;
import com.coder24.effective.exception.EffectiveWriterException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Generic Excel Util class to write data to excel with Maps
 * <p>
 * Author: Orifjon Yunusjonov
 * since: 05/10/2024
 */
@Slf4j
public class SXSSFExcelUtil {
    protected SXSSFWorkbook wb;
    protected short fontSize = 14;
    protected final ForkJoinPool forkJoinPool = new ForkJoinPool();

    protected SXSSFWorkbook getWorkBook(InputStream inputStream) throws IOException {
        return new SXSSFWorkbook(new XSSFWorkbook(inputStream), -1);
    }

    protected void writeToExcel(HttpServletResponse response, List<Map<String, Object>> result, Sheet sheet, Integer fromHeader, ExcelBody<Map<String, Object>> excelBody) throws EffectiveWriterException {
        int chunkSize = ChunkSizeEnum.getChunkSizeForValue(result.size());
        ProcessDataTask task = new ProcessDataTask(result, sheet, fromHeader, excelBody, chunkSize);

        forkJoinPool.invoke(task);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            task.join();
            wb.write(outputStream);
        } catch (Exception e) {
            log.error("Error on processing effective writer - {}", e.getMessage());
            throw new EffectiveWriterException("Error on processing effective writer - " + e.getMessage());
        }
//        finally {
//            System.gc();
//        }

    }

    protected void putIndex(List<Map<String, Object>> result) {
        for (int i = 0; i < result.size(); i++) {
            result.get(i).put("index", i);
        }
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, String value) {
        Cell cell = row.createCell(currentCell, CellType.STRING);

        cell.setCellValue(value != null ? value : "");

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, Integer value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        cell.setCellValue(value == null ? 0 : value);

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, Date value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        cell.setCellValue(value == null ? null : formatter.format(value));

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, Double value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        cell.setCellValue(value == null ? 0.0 : value);

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, BigInteger value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        cell.setCellValue(value == null ? 0.0 : value.doubleValue());

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, Object value) {

        Cell cell = row.createCell(currentCell, CellType.STRING);

        cell.setCellValue(value == null ? "" : value.toString());

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, Long value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        cell.setCellValue(value == null ? 0.0 : value);

        cell.setCellStyle(cellStyle);
    }

    protected void createCell(Row row, Integer currentCell, CellStyle cellStyle, BigDecimal value) {

        Cell cell = row.createCell(currentCell, CellType.NUMERIC);

        cell.setCellValue(value == null ? 0.0 : value.doubleValue());

        cell.setCellStyle(cellStyle);
    }

    protected CellStyle getCellStyle(short fontSize) {
        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
//        fontTitle.set(true);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    protected CellStyle getCellStyle(short fontSize, boolean wrapText) {
        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(false);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(wrapText);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    protected CellStyle getMonyCellStyle(short fontSize) {
        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(false);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    protected CellStyle getHeaderCellStyle(short fontSize) {
        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    protected void getHeader(HttpServletResponse res, String fileName) {
        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.addHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        res.addHeader("Cache-Control", "max-age=1, must-revalidate");
        res.addHeader("Pragma", "no-cache");
//        res.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
//        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, redirectUrl);
//        res.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
//        res.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "48000");
//        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, DELETE");
    }

    protected String getExcelColumnName(Integer columnNumber) {
        int ALPHABET_SIZE = 26;

        int CHARACTER_OFFSET = 65;

        StringBuilder columnName = new StringBuilder();

        int dividend = columnNumber;

        while (dividend > 0) {
            int modulo = (dividend - 1) % ALPHABET_SIZE;

            columnName.insert(0, (char) (modulo + CHARACTER_OFFSET));

            dividend = (int) ((dividend - modulo) / ALPHABET_SIZE);
        }

        return columnName.toString();
    }

    protected void setTitle(Sheet sheet, Integer firstRow, Integer firstCol, Integer lastRow, Integer lastCol, String title, Integer _fontSize) {

        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(_fontSize.shortValue());
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(false);
        fontTitle.setItalic(true);

        CellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setWrapText(true);
        cellStyleTitle.setFont(fontTitle);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        row.createCell(firstCol, CellType.STRING).setCellValue(title);
        CellRangeAddress RangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(RangeAddress);
        row.getCell(firstCol).setCellStyle(cellStyleTitle);
    }

    protected void setTitle(HSSFSheet sheet, Integer firstRow, Integer firstCol, Integer lastRow, Integer lastCol, String title, Integer _fontSize, boolean isBold) {

        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(_fontSize.shortValue());
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(isBold);
        fontTitle.setItalic(true);

        CellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setWrapText(true);
        cellStyleTitle.setFont(fontTitle);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        row.createCell(firstCol, CellType.STRING).setCellValue(title);
        CellRangeAddress RangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(RangeAddress);
        row.getCell(firstCol).setCellStyle(cellStyleTitle);
    }

    protected void changeValue(Sheet sheet, Integer rowPosition, Integer colPosition, String value) {

        Row row = sheet.getRow(rowPosition);
        if (row == null) {
            return;
        }
        Cell cell = row.getCell(colPosition);
        if (cell == null) return;

        cell.setCellValue(value);
    }

    protected void changeValue(HSSFSheet sheet, Integer rowPosition, Integer colPosition, Integer value) {

        Row row = sheet.getRow(rowPosition);
        if (row == null) {
            return;
        }
        Cell cell = row.getCell(colPosition);
        if (cell == null) return;

        cell.setCellValue(value);
    }

    protected void setHeader(HSSFSheet sheet, Integer firstRow, Integer firstCol, Integer lastRow, Integer lastCol, String title, Integer _fontSize) {

        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(_fontSize.shortValue());
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);

        CellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setWrapText(true);
        cellStyleTitle.setFont(fontTitle);

        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }
        row.createCell(firstCol, CellType.STRING).setCellValue(title);
        CellRangeAddress RangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(RangeAddress);
        row.getCell(firstCol).setCellStyle(cellStyleTitle);
    }

    protected void createFormulaRow(Sheet sheet, Integer topRow, Integer currentRow, Integer currentCol, Integer lastCol, Integer _fontSize) {

        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(_fontSize.shortValue());
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        cellStyle.setFont(fontTitle);
        Row row = sheet.createRow(currentRow);

        for (int t = 0; t < currentCol; t++) {
            row.createCell(t, CellType.STRING);
            row.getCell(t).setCellStyle(cellStyle);
        }
        row.getCell(0).setCellValue("ЖАМИ:");
        CellRangeAddress RangeAddress = new CellRangeAddress(currentRow, currentRow, 0, currentCol - 1);
        sheet.addMergedRegion(RangeAddress);
        for (int t = currentCol; t < lastCol; t++) {
            String columnName = getExcelColumnName(t + 1);
            row.createCell(t, CellType.FORMULA);
            row.getCell(t).setCellStyle(cellStyle);
            row.getCell(t).setCellFormula("SUM(" + columnName + (topRow) + ":" + columnName + (currentRow) + ")");
        }
    }


    public CellStyle getCellStyle(Workbook workbook, short fontSize) {
        Font fontTitle = workbook.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    public CellStyle getCellStyleText(Workbook workbook, short fontSize) {
        Font fontTitle = workbook.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(false);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }

    public CellStyle getCellStyleNumber(Workbook workbook, short fontSize) {
        Font fontTitle = workbook.createFont();
        DataFormat format = workbook.createDataFormat();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(false);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));
        return cellStyle;
    }

    public CellStyle getHeaderCellStyle(Workbook workbook, short fontSize) {
        Font fontTitle = workbook.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(fontTitle);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;
    }


    public void setTitle(Workbook wb, Sheet sheet, Integer firstRow, Integer firstCol, Integer lastRow, Integer lastCol, String title, short fontSize) {
        Font fontTitle = wb.createFont();
        fontTitle.setFontHeightInPoints(fontSize);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setBold(true);
        CellStyle cellStyleTitle = wb.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleTitle.setWrapText(true);
        cellStyleTitle.setFont(fontTitle);
        Row row = sheet.getRow(firstRow);
        if (row == null) {
            row = sheet.createRow(firstRow);
        }

        row.createCell(firstCol, CellType.STRING).setCellValue(title);
        CellRangeAddress RangeAddress = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(RangeAddress);
        row.getCell(firstCol).setCellStyle(cellStyleTitle);
    }


}

