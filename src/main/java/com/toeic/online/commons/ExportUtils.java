package com.toeic.online.commons;

import com.toeic.online.config.Constants;
import com.toeic.online.service.dto.*;
import com.toeic.online.service.dto.ExcelColumn;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExportUtils {

    private final DecimalFormat doubleFormat = new DecimalFormat("#.##");
    private final Logger log = LoggerFactory.getLogger(ExportUtils.class);

    public ByteArrayInputStream onExport(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Boolean displayIndex,
        String sheetName
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);
            int diff = this.getDiff(displayIndex);

            Row rowHeader = createFileTitle(startRow, startCol, excelTitle, workbook, sheet, (short) 500, (lstColumn.size() - 1 + diff));

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //trai
            ByteArrayInputStream byteArrayInputStream = createFileOutput(
                lstColumn,
                lstData,
                startRow,
                startCol,
                workbook,
                sheet,
                displayIndex
            );
            return byteArrayInputStream;
        }
    }

    public ByteArrayInputStream onExport(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Boolean displayIndex
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("sheet 1");
            int diff = this.getDiff(displayIndex);

            Row rowHeader = createFileTitle(startRow, startCol, excelTitle, workbook, sheet, (short) 500, (lstColumn.size() - 1 + diff));

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //trai
            ByteArrayInputStream byteArrayInputStream = createFileOutput(
                lstColumn,
                lstData,
                startRow,
                startCol,
                workbook,
                sheet,
                displayIndex
            );
            return byteArrayInputStream;
        }
    }

    public ByteArrayInputStream onExportSubjectClass(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Boolean displayIndex
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            int diff = this.getDiff(displayIndex);

            Row rowHeader = createFileTitleSubjectClass(
                startRow,
                startCol,
                excelTitle,
                workbook,
                sheet,
                (short) 500,
                (lstColumn.size() - 1 + diff)
            );

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //trai
            ByteArrayInputStream byteArrayInputStream = createFileOutput(
                lstColumn,
                lstData,
                startRow,
                startCol,
                workbook,
                sheet,
                displayIndex
            );
            return byteArrayInputStream;
        }
    }

    public Row createFileTitle(
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Workbook workbook,
        Sheet sheet,
        short rowHeight,
        int numCol
    ) {
        int rowTitle = startRow > 3 ? startRow - 3 : 0;
        if (excelTitle != null) {
            if (!DataUtil.isNullOrEmpty(excelTitle.getTitle())) {
                Row rowMainTitle = sheet.createRow(rowTitle);
                Cell mainCellTitle = rowMainTitle.createCell(startCol);
                mainCellTitle.setCellValue(excelTitle.getTitle().toUpperCase());
                CellStyle cellStyleTitle = getCellStyleTitle(workbook);
                Font newFont = mainCellTitle.getSheet().getWorkbook().createFont();
                newFont.setFontHeightInPoints((short) 18);
                newFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                cellStyleTitle.setFont(newFont);
                mainCellTitle.setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(rowTitle, rowTitle, startCol, numCol));
            }
            if (!DataUtil.isNullOrEmpty(excelTitle.getDateExportPattern()) && !DataUtil.isNullOrEmpty(excelTitle.getDateExportTitle())) {
                Row rowDateExport = sheet.createRow(rowTitle + 1);
                Cell mainCellTitle = rowDateExport.createCell((numCol / 2));
                mainCellTitle.setCellValue(
                    excelTitle.getDateExportTitle() + " : " + DataUtil.dateToString(new Date(), excelTitle.getDateExportPattern())
                );
                CellStyle cellStyleHeader = createCellStyle(workbook);
                Font hSSFFontHeader = createFontHeader(workbook);
                hSSFFontHeader.setBold(true);
                cellStyleHeader.setFont(hSSFFontHeader);
                mainCellTitle.setCellStyle(cellStyleHeader);
            }
        }
        //Header
        Row rowHeader = sheet.createRow(startRow);
        rowHeader.setHeight(rowHeight);

        return rowHeader;
    }

    private Row createFileReports(
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Workbook workbook,
        Sheet sheet,
        short rowHeight,
        int numCol
    ) {
        int rowTitle = startRow > 4 ? startRow - 4 : 0;
        if (excelTitle != null) {
            if (!DataUtil.isNullOrEmpty(excelTitle.getTitle())) {
                Row rowMainTitle = sheet.createRow(rowTitle);
                Cell mainCellTitle = rowMainTitle.createCell(startCol);
                mainCellTitle.setCellValue(excelTitle.getTitle().toUpperCase());
                CellStyle cellStyleTitle = getCellStyleTitle(workbook);
                Font newFont = mainCellTitle.getSheet().getWorkbook().createFont();
                newFont.setFontHeightInPoints((short) 18);
                newFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                cellStyleTitle.setFont(newFont);
                mainCellTitle.setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(rowTitle, rowTitle, startCol, numCol));
            }
            if (!DataUtil.isNullOrEmpty(excelTitle.getDateExportPattern()) && !DataUtil.isNullOrEmpty(excelTitle.getDateExportTitle())) {
                Row rowDateExport = sheet.createRow(rowTitle + 1);
                Cell mainCellTitle = rowDateExport.createCell(startCol);
                mainCellTitle.setCellValue(
                    excelTitle.getDateExportTitle() + " : " + DataUtil.dateToString(new Date(), excelTitle.getDateExportPattern())
                );
                CellStyle cellStyle = createStyle(workbook);
                mainCellTitle.setCellStyle(cellStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, startCol, numCol));
            }
        }
        //Header
        Row rowHeader = sheet.createRow(startRow);
        rowHeader.setHeight(rowHeight);

        return rowHeader;
    }

    private Row createFileTitleSubjectClass(
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Workbook workbook,
        Sheet sheet,
        short rowHeight,
        int numCol
    ) {
        int rowTitle = startRow > 3 ? startRow - 3 : 0;
        if (excelTitle != null) {
            if (!DataUtil.isNullOrEmpty(excelTitle.getTitle())) {
                Row rowMainTitle = sheet.createRow(rowTitle);
                Cell mainCellTitle = rowMainTitle.createCell(startCol);
                mainCellTitle.setCellValue(excelTitle.getTitle().toUpperCase());
                CellStyle cellStyleTitle = getCellStyleTitle(workbook);
                Font newFont = mainCellTitle.getSheet().getWorkbook().createFont();
                newFont.setFontHeightInPoints((short) 18);
                newFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
                cellStyleTitle.setFont(newFont);
                mainCellTitle.setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(new CellRangeAddress(rowTitle, rowTitle, startCol, numCol));
            }
            if (!DataUtil.isNullOrEmpty(excelTitle.getDateExportPattern()) && !DataUtil.isNullOrEmpty(excelTitle.getDateExportTitle())) {
                Row rowDateExport = sheet.createRow(rowTitle + 1);
                Cell mainCellTitle = rowDateExport.createCell(startCol);
                mainCellTitle.setCellValue(
                    excelTitle.getDateExportTitle() + " : " + DataUtil.dateToString(new Date(), excelTitle.getDateExportPattern())
                );
                CellStyle cellStyle = createStyle(workbook);
                mainCellTitle.setCellStyle(cellStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, startCol, numCol));
            }
        }
        //Header
        Row rowHeader = sheet.createRow(startRow);
        rowHeader.setHeight(rowHeight);

        return rowHeader;
    }

    private CellStyle createCellStyleHeader(Workbook workbook) {
        CellStyle cellStyleHeader = workbook.createCellStyle();
        cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleHeader.setBorderLeft(BorderStyle.THIN);
        cellStyleHeader.setBorderBottom(BorderStyle.THIN);
        cellStyleHeader.setBorderRight(BorderStyle.THIN);
        cellStyleHeader.setBorderTop(BorderStyle.THIN);
        cellStyleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyleHeader.setWrapText(true);
        return cellStyleHeader;
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle cellStyleHeader = workbook.createCellStyle();
        cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyleHeader;
    }

    private Font createFontHeader(Workbook workbook) {
        Font hSSFFontHeader = workbook.createFont();
        hSSFFontHeader.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFontHeader.setFontHeightInPoints((short) 10);
        hSSFFontHeader.setBold(true);
        return hSSFFontHeader;
    }

    public CellStyle createStyleHeader(Workbook workbook) {
        CellStyle cellStyleHeader = createCellStyleHeader(workbook);
        Font hSSFFontHeader = createFontHeader(workbook);
        hSSFFontHeader.setColor(IndexedColors.BLACK.index);
        cellStyleHeader.setFont(hSSFFontHeader);
        return cellStyleHeader;
    }

    private CellStyle createStyle(Workbook workbook) {
        CellStyle cellStyleHeader = createCellStyle(workbook);
        Font hSSFFontHeader = createFontHeader(workbook);
        cellStyleHeader.setFont(hSSFFontHeader);
        return cellStyleHeader;
    }

    private ByteArrayInputStream createFileOutput(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        Workbook workbook,
        Sheet sheet,
        Boolean displayIndex
    ) throws Exception {
        //trai
        CellStyle cellStyleLeft = getCellStyle(workbook, HorizontalAlignment.LEFT);
        //phai
        CellStyle cellStyleRight = getCellStyle(workbook, HorizontalAlignment.RIGHT);
        //giua
        CellStyle cellStyleCenter = getCellStyle(workbook, HorizontalAlignment.CENTER);

        writeDataReport(lstColumn, lstData, startRow, startCol, sheet, cellStyleLeft, cellStyleRight, cellStyleCenter, displayIndex);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public CellStyle getCellStyle(Workbook workbook, HorizontalAlignment horizontalAlignment) {
        CellStyle cellStyleCenter = workbook.createCellStyle();
        cellStyleCenter.setAlignment(horizontalAlignment);
        cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleCenter.setBorderLeft(BorderStyle.THIN);
        cellStyleCenter.setBorderBottom(BorderStyle.THIN);
        cellStyleCenter.setBorderRight(BorderStyle.THIN);
        cellStyleCenter.setBorderTop(BorderStyle.THIN);
        cellStyleCenter.setWrapText(true);
        cellStyleCenter.setDataFormat((short) BuiltinFormats.getBuiltinFormat("@"));
        return cellStyleCenter;
    }

    private CellStyle getCellStyleTitle(Workbook workbook) {
        CellStyle cellStyleTitle = workbook.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleTitle.setFillForegroundColor(IndexedColors.GREEN.index);
        cellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font hSSFFont = workbook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 20);
        hSSFFont.setBold(true);
        hSSFFont.setColor(IndexedColors.WHITE.index);
        cellStyleTitle.setFont(hSSFFont);
        return cellStyleTitle;
    }

    private void writeDataReport(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        Sheet sheet,
        CellStyle cellStyleLeft,
        CellStyle cellStyleRight,
        CellStyle cellStyleCenter,
        Boolean displayIndex
    ) throws IllegalAccessException {
        if (lstData != null && !lstData.isEmpty()) {
            Object firstRow = lstData.get(0);
            Map<String, Field> mapField = new HashMap<>();
            for (ExcelColumn column : lstColumn) {
                String header = column.getColumn();
                Field[] fs = ReflectorUtil.getAllFields(firstRow.getClass());
                Arrays
                    .stream(fs)
                    .peek(f -> f.setAccessible(true))
                    .filter(f -> f.getName().equals(header))
                    .forEach(f -> mapField.put(header, f));
            }

            int diff = this.getDiff(displayIndex);
            for (int i = 0; i < lstData.size(); i++) {
                Row row = sheet.createRow(i + startRow + 1);
                if (displayIndex) {
                    Cell cell = row.createCell(startCol);
                    cell.setCellValue(i + 1);
                    cell.setCellStyle(cellStyleCenter);
                }
                for (int j = 0; j < lstColumn.size(); j++) {
                    Cell cell = row.createCell(j + startCol + diff);
                    ExcelColumn column = lstColumn.get(j);
                    Object obj = lstData.get(i);
                    Field f = mapField.get(column.getColumn());
                    if (f != null) {
                        Object value = f.get(obj);
                        String text;
                        if (value instanceof Double) {
                            text = doubleToString((Double) value);
                        } else if (value instanceof Instant) {
                            text = instantToString((Instant) value, column.getPattern());
                        } else if (value instanceof Date) {
                            text = dateToString((Date) value, column.getPattern());
                        } else {
                            text = objectToString(value);
                        }
                        cell.setCellValue(text);
                        this.setCellStyle(cell, column, cellStyleLeft, cellStyleRight, cellStyleCenter);
                    }
                }
            }
        }
    }

    private static boolean isDouble(String param) {
        try {
            Double.parseDouble(param);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void setCellStyle(Cell cell, ExcelColumn column, CellStyle cellStyleLeft, CellStyle cellStyleRight, CellStyle cellStyleCenter) {
        if (ExcelColumn.ALIGN_MENT.CENTER.equals(column.getAlign())) {
            cell.setCellStyle(cellStyleCenter);
        }
        if (ExcelColumn.ALIGN_MENT.LEFT.equals(column.getAlign())) {
            cell.setCellStyle(cellStyleLeft);
        }
        if (ExcelColumn.ALIGN_MENT.RIGHT.equals(column.getAlign())) {
            cell.setCellStyle(cellStyleRight);
        }
    }

    private String instantToString(Instant value, String pattern) {
        if (pattern != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
            return dtf.format(LocalDateTime.ofInstant(value, ZoneId.of(Constants.TIME_ZONE_DEFAULT)));
        }
        return "";
    }

    private String dateToString(Date value, String pattern) {
        if (pattern != null) {
            SimpleDateFormat dtf = new SimpleDateFormat(pattern);
            return dtf.format(value);
        }
        return "";
    }

    private String objectToString(Object value) {
        return (value == null) ? "" : value.toString();
    }

    private String doubleToString(Double value) {
        if (value == null) {
            return "";
        }
        String result = doubleFormat.format(value);
        if (result.endsWith(".0")) {
            result = result.split("\\.")[0];
        }
        return result;
    }

    private int getDiff(Boolean displayIndex) {
        return Boolean.TRUE.equals(displayIndex) ? 1 : 0;
    }

    public String getRowString(Row row, int col) {
        String result = null;
        Cell cell = row.getCell(col);
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    result = new DecimalFormat("#.#").format(cell.getNumericCellValue());
                    if (result.endsWith(".0")) {
                        result = result.substring(0, result.lastIndexOf("."));
                    }
                    break;
                case STRING:
                    result = cell.getStringCellValue();
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * @param lstColumn
     * @param lstData
     * @param startRow
     * @param startCol
     * @param excelTitle
     * @param displayIndex
     * @return
     * @throws Exception
     */
    public ByteArrayInputStream onExport2Sheet(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        List<?> lstData1,
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Boolean displayIndex,
        String titleSheet1,
        String titleSheet2
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            //Create Sheet1:
            Sheet sheet = workbook.createSheet(titleSheet1);
            int diff = this.getDiff(displayIndex);

            Row rowHeader = createFileTitle(startRow, startCol, excelTitle, workbook, sheet, (short) 500, (lstColumn.size() - 1 + diff));

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //Create Sheet2:
            Sheet sheet1 = workbook.createSheet(titleSheet2);

            rowHeader = createFileTitle(startRow, startCol, excelTitle, workbook, sheet1, (short) 500, (lstColumn.size() - 1 + diff));

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger1 = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet1.setColumnWidth(startCol + diff + atomicInteger1.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //trai
            ByteArrayInputStream byteArrayInputStream = createFileOutput(
                lstColumn,
                lstData,
                lstData1,
                startRow,
                startCol,
                workbook,
                sheet,
                sheet1,
                displayIndex
            );
            return byteArrayInputStream;
        }
    }

    /**
     * @param lstColumn
     * @param lstData
     * @param startRow
     * @param startCol
     * @param workbook
     * @param sheet1       * @param sheet2
     * @param displayIndex
     * @return
     * @throws Exception
     */
    private ByteArrayInputStream createFileOutput(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        List<?> lstData1,
        int startRow,
        int startCol,
        Workbook workbook,
        Sheet sheet1,
        Sheet sheet2,
        Boolean displayIndex
    ) throws Exception {
        //trai
        CellStyle cellStyleLeft = getCellStyle(workbook, HorizontalAlignment.LEFT);
        //phai
        CellStyle cellStyleRight = getCellStyle(workbook, HorizontalAlignment.RIGHT);
        //giua
        CellStyle cellStyleCenter = getCellStyle(workbook, HorizontalAlignment.CENTER);

        writeDataReport(lstColumn, lstData, startRow, startCol, sheet1, cellStyleLeft, cellStyleRight, cellStyleCenter, displayIndex);
        writeDataReport(lstColumn, lstData1, startRow, startCol, sheet2, cellStyleLeft, cellStyleRight, cellStyleCenter, displayIndex);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    private CellStyle getTitleStyle(Workbook workbook) {
        CellStyle cellStyleTitle = workbook.createCellStyle();
        cellStyleTitle.setAlignment(HorizontalAlignment.LEFT);
        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        //        cellStyleTitle.setFillForegroundColor(IndexedColors.GREEN.index);
        //        cellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font hSSFFont = workbook.createFont();
        hSSFFont.setFontName(HSSFFont.FONT_ARIAL);
        hSSFFont.setFontHeightInPoints((short) 15);
        hSSFFont.setBold(true);
        hSSFFont.setColor(IndexedColors.BLACK.index);
        cellStyleTitle.setFont(hSSFFont);
        return cellStyleTitle;
    }

    /**
     * @param lstColumn
     * @param lstData
     * @param startRow
     * @param startCol
     * @param excelTitle
     * @param displayIndex
     * @return
     * @throws Exception
     */
    public ByteArrayInputStream onExport2Table(
        List<ExcelColumn> lstColumn,
        List<ExcelColumn> lstColumn1,
        List<?> lstData,
        List<?> lstData1,
        int startRow,
        int startCol,
        ExcelTitle excelTitle,
        Boolean displayIndex,
        String title1,
        String title2,
        String titleSheet1
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            //Create Sheet1:
            Sheet sheet = workbook.createSheet(titleSheet1);
            int diff = this.getDiff(displayIndex);

            //Create Table1:
            Row rowHeader = createFileTitle(startRow, startCol, excelTitle, workbook, sheet, (short) 500, (lstColumn.size() - 1 + diff));

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );
            //trai
            CellStyle cellStyleLeft = getCellStyle(workbook, HorizontalAlignment.LEFT);
            //phai
            CellStyle cellStyleRight = getCellStyle(workbook, HorizontalAlignment.RIGHT);
            //giua
            CellStyle cellStyleCenter = getCellStyle(workbook, HorizontalAlignment.CENTER);

            writeDataReport(lstColumn, lstData, startRow, startCol, sheet, cellStyleLeft, cellStyleRight, cellStyleCenter, displayIndex);

            //Create Table2:
            startRow = startRow + lstData.size() + 2;
            Row rowMainTitle = sheet.createRow(startRow);
            Cell mainCellTitle = rowMainTitle.createCell(startCol);
            mainCellTitle.setCellValue(title2);
            CellStyle cellStyleTitle = getTitleStyle(workbook);
            mainCellTitle.setCellStyle(cellStyleTitle);
            if (!DataUtil.isNullOrEmpty(lstColumn1)) {
                sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, startCol, (lstColumn1.size() - 1 + diff)));
            }
            startRow = startRow + 1;
            if (!DataUtil.isNullOrEmpty(lstColumn1)) {
                rowHeader = createFileTitle(startRow, startCol, null, workbook, sheet, (short) 500, (lstColumn1.size() - 1 + diff));
            }
            cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue("STT");
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn1.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn1.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger1 = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger1.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            writeDataReport(lstColumn1, lstData1, startRow, startCol, sheet, cellStyleLeft, cellStyleRight, cellStyleCenter, displayIndex);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream onExportStudent(
        List<ExcelColumn> lstColumn,
        List<?> lstData,
        int startRow,
        int startCol,
        String title,
        String years,
        Boolean displayIndex
    ) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            int diff = this.getDiff(displayIndex);

            Font hSSFFontHeader = workbook.createFont();
            hSSFFontHeader.setFontName(HSSFFont.FONT_ARIAL);
            hSSFFontHeader.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
            hSSFFontHeader.setFontHeightInPoints((short) 14);
            hSSFFontHeader.setBold(true);

            Row rowHeader1 = sheet.createRow(0);
            Cell cellHeader1 = rowHeader1.createCell(0);
            CellStyle style = workbook.createCellStyle();

            style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);

            style.setFont(hSSFFontHeader);
            cellHeader1.setCellStyle(style);
            cellHeader1.setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 9));

            Font hSSFFontTitle = workbook.createFont();
            hSSFFontTitle.setFontName(HSSFFont.FONT_ARIAL);
            hSSFFontTitle.setFontHeightInPoints((short) 13);
            hSSFFontTitle.setBold(true);

            Row rowHeader2 = sheet.createRow(1);
            Cell cellHeader2 = rowHeader2.createCell(0);
            CellStyle styleTitle = workbook.createCellStyle();
            styleTitle.setAlignment(HorizontalAlignment.CENTER);
            styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            styleTitle.setFont(hSSFFontTitle);
            cellHeader2.setCellStyle(styleTitle);
            cellHeader2.setCellValue(years);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            Row rowHeader = createFileTitleStudent(startRow, sheet, (short) 500);

            CellStyle cellStyleHeader = createStyleHeader(workbook);

            if (Boolean.TRUE.equals(displayIndex)) {
                Cell cellIndex = rowHeader.createCell(startCol);
                cellIndex.setCellValue(Translator.toLocale("recordNo"));
                cellIndex.setCellStyle(cellStyleHeader);
            }
            for (int i = 0; i < lstColumn.size(); i++) {
                Cell cellHeader = rowHeader.createCell(i + startCol + diff);
                cellHeader.setCellValue(lstColumn.get(i).getTitle());
                cellHeader.setCellStyle(cellStyleHeader);
            }
            AtomicInteger atomicInteger = new AtomicInteger(0);
            lstColumn.forEach(
                e -> {
                    if (e.getColumnWidth() != null) {
                        sheet.setColumnWidth(startCol + diff + atomicInteger.getAndIncrement(), e.getColumnWidth());
                    }
                }
            );

            //trai
            ByteArrayInputStream byteArrayInputStream = createFileOutput(
                lstColumn,
                lstData,
                startRow,
                startCol,
                workbook,
                sheet,
                displayIndex
            );
            return byteArrayInputStream;
        }
    }

    private Row createFileTitleStudent(int startRow, Sheet sheet, short rowHeight) {
        //Header
        Row rowHeader = sheet.createRow(startRow);
        rowHeader.setHeight(rowHeight);

        return rowHeader;
    }

    public Font getFontHeaderBoldRed(Workbook workbook) {
        Font fontHeaderBoldRed = workbook.createFont();
        fontHeaderBoldRed.setBold(true);
        fontHeaderBoldRed.setFontName("Times New Roman");
        fontHeaderBoldRed.setFontHeightInPoints(new Short("12"));
        fontHeaderBoldRed.setColor(Font.COLOR_RED);
        return fontHeaderBoldRed;
    }

    public Font getFontHeaderBold(Workbook workbook) {
        Font fontHeaderBold = workbook.createFont();
        fontHeaderBold.setBold(true);
        fontHeaderBold.setFontName("Times New Roman");
        fontHeaderBold.setFontHeightInPoints(new Short("12"));
        return fontHeaderBold;
    }

    public Font getFontHeader(Workbook workbook) {
        Font fontHeaderBold = workbook.createFont();
        fontHeaderBold.setBold(false);
        fontHeaderBold.setFontName("Times New Roman");
        fontHeaderBold.setFontHeightInPoints(new Short("12"));
        return fontHeaderBold;
    }

    public Font getFontTimeNewRoman(Workbook workbook) {
        Font fontTimeNewRoman = workbook.createFont();
        fontTimeNewRoman.setBold(true);
        fontTimeNewRoman.setFontName("Times New Roman");
        fontTimeNewRoman.setFontHeightInPoints(new Short("12"));
        return fontTimeNewRoman;
    }

    public CellStyle getStyleHeader(Workbook wb, Font fontTimeNewRoman) {
        CellStyle styleHeader = wb.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setWrapText(true);
        styleHeader.setFont(fontTimeNewRoman);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        return styleHeader;
    }

    public CellStyle getErrorStyle(Workbook workbook) {
        CellStyle errStyle = workbook.createCellStyle();
        errStyle.setWrapText(true);
        errStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        errStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        errStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        errStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        errStyle.setBorderTop(BorderStyle.THIN);
        errStyle.setBorderBottom(BorderStyle.THIN);
        errStyle.setBorderRight(BorderStyle.THIN);
        errStyle.setBorderLeft(BorderStyle.THIN);
        Font fontDefault = workbook.createFont();
        fontDefault.setColor(IndexedColors.RED.getIndex());
        errStyle.setFont(fontDefault);
        return errStyle;
    }

    public CellStyle getStyleErrTitle(Workbook workbook) {
        CellStyle styleTitleErr = workbook.createCellStyle();
        styleTitleErr.setTopBorderColor(IndexedColors.BLACK.getIndex());
        styleTitleErr.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styleTitleErr.setRightBorderColor(IndexedColors.BLACK.getIndex());
        styleTitleErr.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        styleTitleErr.setBorderTop(BorderStyle.THIN);
        styleTitleErr.setBorderBottom(BorderStyle.THIN);
        styleTitleErr.setBorderRight(BorderStyle.THIN);
        styleTitleErr.setBorderLeft(BorderStyle.THIN);
        Font fontTitleErr = workbook.createFont();
        fontTitleErr.setColor(IndexedColors.RED.getIndex());
        fontTitleErr.setBold(true);
        styleTitleErr.setFont(fontTitleErr);
        styleTitleErr.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitleErr.setAlignment(HorizontalAlignment.CENTER);
        return styleTitleErr;
    }

    public CellStyle getStyleErrorBorder(Workbook workbook) {
        CellStyle styleErr = workbook.createCellStyle();
        styleErr.setBorderTop(BorderStyle.MEDIUM);
        styleErr.setTopBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderBottom(BorderStyle.MEDIUM);
        styleErr.setBottomBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderRight(BorderStyle.MEDIUM);
        styleErr.setRightBorderColor(IndexedColors.RED.getIndex());
        styleErr.setBorderLeft(BorderStyle.MEDIUM);
        styleErr.setLeftBorderColor(IndexedColors.RED.getIndex());
        return styleErr;
    }
}
