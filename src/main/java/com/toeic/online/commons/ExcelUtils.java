package com.toeic.online.commons;

import com.toeic.online.service.dto.ExcelDynamicDTO;
import com.toeic.online.service.dto.ListExcelDynamicDTO;
import com.toeic.online.service.dto.ListExcelDynamicGroupDTO;
import com.toeic.online.service.dto.ObjectTemplate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

@Component
public class ExcelUtils {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int WIDTH = 255;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public <E> ByteArrayInputStream export(
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws Exception {
        return createWorkbook("Data", obj, header, column, title, lsSize, lsAlign, underHeader);
    }

    public <E> ByteArrayInputStream export(
        String sheetName,
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws Exception {
        return createWorkbook(sheetName, obj, header, column, title, lsSize, lsAlign, underHeader);
    }

    public <E> ByteArrayInputStream createWorkbook(
        String sheetName,
        List<E> obj,
        List<String> header,
        List<String> column,
        String title,
        List<Integer> lsSize,
        List<Integer> lsAlign,
        String... underHeader
    ) throws Exception {
        // 1. Set Title
        XSSFWorkbook my_workbook = new XSSFWorkbook();
        XSSFSheet my_sheet = my_workbook.createSheet(sheetName);

        // Set column width
        for (int i = 0; i < header.size(); i++) {
            my_sheet.setColumnWidth(1 + i, lsSize.get(i));
        }

        CellStyle styleTitle = my_workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setFillForegroundColor(IndexedColors.GREEN.index);
        styleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont fontTitle = my_workbook.createFont();
        fontTitle.setFontHeightInPoints((short) 15);
        fontTitle.setBold(true);
        fontTitle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        fontTitle.setFontName("Times New Roman");
        styleTitle.setFont(fontTitle);

        XSSFFont fontHeader = my_workbook.createFont();
        fontHeader.setFontHeightInPoints((short) 13);
        fontHeader.setBold(true);
        fontHeader.setFontName("Times New Roman");

        XSSFFont fontRows = my_workbook.createFont();
        fontRows.setFontHeightInPoints((short) 11);
        fontRows.setFontName("Times New Roman");

        XSSFCellStyle styleHeader = my_workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        byte[] rgb = new byte[3];
        rgb[0] = (byte) 149;
        rgb[1] = (byte) 214;
        rgb[2] = (byte) 242;
        XSSFColor color = new XSSFColor(rgb, new DefaultIndexedColorMap());
        styleHeader.setFillForegroundColor(color);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setFont(fontHeader);
        styleHeader.setWrapText(true);

        CellStyle styleParam = my_workbook.createCellStyle();
        styleParam.setAlignment(HorizontalAlignment.CENTER);
        styleParam.setFont(fontHeader);
        styleParam.setWrapText(true);

        CellStyle styleNormal = my_workbook.createCellStyle();
        styleNormal.setBorderRight(BorderStyle.THIN);
        styleNormal.setBorderLeft(BorderStyle.THIN);
        styleNormal.setBorderBottom(BorderStyle.THIN);
        styleNormal.setBorderTop(BorderStyle.THIN);
        styleNormal.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNormal.setWrapText(true);
        styleNormal.setFont(fontRows);

        CellStyle styleNumber = my_workbook.createCellStyle();
        styleNumber.setAlignment(HorizontalAlignment.CENTER);
        styleNumber.setBorderRight(BorderStyle.THIN);
        styleNumber.setBorderLeft(BorderStyle.THIN);
        styleNumber.setBorderBottom(BorderStyle.THIN);
        styleNumber.setBorderTop(BorderStyle.THIN);
        styleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNumber.setFont(fontRows);
        styleHeader.setWrapText(true);

        CellStyle styleNumberRight = my_workbook.createCellStyle();
        styleNumberRight.setAlignment(HorizontalAlignment.RIGHT);
        styleNumberRight.setBorderRight(BorderStyle.THIN);
        styleNumberRight.setBorderLeft(BorderStyle.THIN);
        styleNumberRight.setBorderBottom(BorderStyle.THIN);
        styleNumberRight.setBorderTop(BorderStyle.THIN);
        styleNumberRight.setVerticalAlignment(VerticalAlignment.CENTER);
        styleNumberRight.setWrapText(true);
        styleNumberRight.setFont(fontRows);

        Row row = my_sheet.createRow((short) 1);
        Cell cell = row.createCell((short) 0);
        cell.setCellValue(title);
        cell.setCellStyle(styleTitle);

        //Date export
        //        CellStyle dateExportStyle = my_workbook.createCellStyle();
        //        dateExportStyle.setAlignment(HorizontalAlignment.CENTER);
        //        dateExportStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //        short start = 4;
        //        row = my_sheet.createRow((short) 2);
        //        cell = row.createCell(start-1);
        //        cell.setCellStyle(dateExportStyle);
        //        cell.setCellValue(Translator.toLocale("common.dateExport"));
        //        cell = row.createCell(start);
        //        cell.setCellStyle(dateExportStyle);
        //        cell.setCellValue(LocalDate.now().format(DATE_FORMAT));

        my_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, column.size()));

        //set param
        for (int i = 0; i < underHeader.length; i++) {
            Row rowP = my_sheet.createRow((short) 3 + i);
            Cell cellP = rowP.createCell((short) 0);
            cellP.setCellValue(underHeader[i]);
            cellP.setCellStyle(styleParam);
            my_sheet.addMergedRegion(new CellRangeAddress(3 + i, 3 + i, 0, column.size()));
        }

        // 2. Set Header
        // So thu tu
        row = my_sheet.createRow((short) 4 + underHeader.length);
        cell = row.createCell(0);
        cell.setCellValue("STT");
        cell.setCellStyle(styleHeader);
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(styleHeader);
        }

        // 3. Set detail
        for (int k = 0; k < obj.size(); k++) {
            row = my_sheet.createRow((short) k + 5 + underHeader.length);

            // STT
            cell = row.createCell(0);
            int stt = k + 1;
            cell.setCellValue(stt);
            cell.setCellStyle(styleNumber);

            /////////////////////
            Object valueObj = obj.get(k);
            Class c1 = valueObj.getClass();
            Field[] valueObjFields = c1.getDeclaredFields();

            for (int c = 0; c < column.size(); c++) {
                for (int i = 0; i < valueObjFields.length; i++) {
                    String fieldName = valueObjFields[i].getName();
                    if (fieldName.equals(column.get(c))) {
                        valueObjFields[i].setAccessible(true);
                        Object newObj = valueObjFields[i].get(valueObj);
                        try {
                            int d = Integer.parseInt(newObj.toString());
                            cell = row.createCell(c + 1);
                            cell.setCellValue(com.toeic.online.commons.DataUtil.nvl(newObj, ""));
                        } catch (Exception ex) {
                            try {
                                BigDecimal d = new BigDecimal(newObj.toString());
                                cell = row.createCell(c + 1);
                                DecimalFormat df2 = new DecimalFormat("###,###,###,###.####");
                                cell.setCellValue(df2.format(d));
                            } catch (Exception e) {
                                cell = row.createCell(c + 1);
                                cell.setCellValue(com.toeic.online.commons.DataUtil.nvl(newObj, ""));
                            }
                        }
                        if (lsAlign.get(c) == 0) {
                            cell.setCellStyle(styleNormal);
                        }
                        if (lsAlign.get(c) == 1) {
                            cell.setCellStyle(styleNumber);
                        }
                        if (lsAlign.get(c) == 2) {
                            cell.setCellStyle(styleNumberRight);
                        }

                        break;
                    }
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //FileOutputStream fileOut = new FileOutputStream("export-test.xlsx");
        //my_workbook.write(fileOut);
        my_workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream buildExcelDynamic(List<ExcelDynamicDTO> listDTO, String templateName) throws Exception {
        ObjectTemplate o = new ObjectTemplate();
        Class c1 = o.getClass();
        Field[] valueObjFields = c1.getDeclaredFields();

        //Chuyen tu DTO sang template
        int k = 0;
        for (ExcelDynamicDTO t : listDTO) {
            k++;
            Field cc = o.getClass().getDeclaredField("c" + k);
            cc.setAccessible(true);
            cc.set(o, t.getId() + "-" + t.getName());
        }

        //Write Excel
        XSSFWorkbook my_workbook = new XSSFWorkbook();

        XSSFFont fontHeader = my_workbook.createFont();
        fontHeader.setFontHeightInPoints((short) 13);
        fontHeader.setBold(true);
        fontHeader.setFontName("Times New Roman");

        XSSFCellStyle styleHeader = my_workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        //////////////

        XSSFSheet my_sheet = my_workbook.createSheet("sheet1");
        Row row = my_sheet.createRow((short) 0);
        Cell cell = null;
        for (int i = 0; i < listDTO.size(); i++) {
            cell = row.createCell(i);
            my_sheet.setColumnWidth(i, 30 * 256);
            for (int j = 0; j < valueObjFields.length; j++) {
                String fieldName = valueObjFields[j].getName();
                if (fieldName.equals("c" + (i + 1))) {
                    valueObjFields[j].setAccessible(true);
                    Object newObj = valueObjFields[j].get(o);
                    cell.setCellValue(newObj.toString());
                    cell.setCellStyle(styleHeader);
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        my_workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    public ByteArrayInputStream buildExcelDynamic(ListExcelDynamicGroupDTO data, String templateName) throws Exception {
        List<ListExcelDynamicDTO> lsData = data.getLsData();
        ObjectTemplate o = new ObjectTemplate();
        Class c1 = o.getClass();
        Field[] valueObjFields = c1.getDeclaredFields();

        //Chuyen tu DTO sang template
        int k = 0;
        List<ExcelDynamicDTO> header = data.getLsData().get(0).getLsDynamicDTO();
        for (ExcelDynamicDTO t : header) {
            k++;
            Field cc = o.getClass().getDeclaredField("c" + k);
            cc.setAccessible(true);
            cc.set(o, t.getName());
        }
        //Write Excel
        XSSFWorkbook my_workbook = new XSSFWorkbook();
        XSSFFont fontHeader = my_workbook.createFont();
        fontHeader.setFontHeightInPoints((short) 13);
        fontHeader.setBold(true);
        fontHeader.setFontName("Times New Roman");

        XSSFCellStyle styleHeader = my_workbook.createCellStyle();
        styleHeader.setAlignment(HorizontalAlignment.CENTER);
        styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        styleHeader.setBorderRight(BorderStyle.THIN);
        styleHeader.setBorderLeft(BorderStyle.THIN);
        styleHeader.setBorderBottom(BorderStyle.THIN);
        styleHeader.setBorderTop(BorderStyle.THIN);
        styleHeader.setFillBackgroundColor(IndexedColors.GREEN.index);
        styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        styleHeader.setWrapText(true);
        //////////////
        XSSFSheet my_sheet = my_workbook.createSheet("sheet1");
        Row row = my_sheet.createRow((short) 0);
        Cell cell = null;
        for (int i = 0; i < header.size(); i++) {
            cell = row.createCell(i);
            my_sheet.setColumnWidth(i, 30 * 256);
            for (int j = 0; j < valueObjFields.length; j++) {
                String fieldName = valueObjFields[j].getName();
                if (fieldName.equals("c" + (i + 1))) {
                    valueObjFields[j].setAccessible(true);
                    Object newObj = valueObjFields[j].get(o);
                    cell.setCellValue(newObj.toString());
                    cell.setCellStyle(styleHeader);
                }
            }
        }
        //////////////////////////////////////////////////
        //Data
        //////////////////////////////////////////////////
        XSSFFont fontDetail = my_workbook.createFont();
        fontDetail.setFontHeightInPoints((short) 13);
        fontDetail.setBold(true);
        fontDetail.setFontName("Times New Roman");

        XSSFCellStyle styleDetail = my_workbook.createCellStyle();
        styleDetail.setAlignment(HorizontalAlignment.CENTER);
        styleDetail.setVerticalAlignment(VerticalAlignment.CENTER);
        styleDetail.setBorderRight(BorderStyle.THIN);
        styleDetail.setBorderLeft(BorderStyle.THIN);
        styleDetail.setBorderBottom(BorderStyle.THIN);
        styleDetail.setBorderTop(BorderStyle.THIN);
        //////////////
        int rowIndex = 0;
        for (ListExcelDynamicDTO data1 : lsData) {
            rowIndex++;
            ObjectTemplate obj = new ObjectTemplate();
            Class classObj = obj.getClass();
            Field[] field = classObj.getDeclaredFields();

            k = 0;
            for (ExcelDynamicDTO t : data1.getLsDynamicDTO()) {
                k++;
                Field cc = obj.getClass().getDeclaredField("c" + k);
                cc.setAccessible(true);
                cc.set(obj, t.getValue());
            }
            row = my_sheet.createRow((short) rowIndex);
            cell = null;
            for (int i = 0; i < header.size(); i++) {
                cell = row.createCell(i);
                my_sheet.setColumnWidth(i, 30 * 256);
                for (int j = 0; j < field.length; j++) {
                    String fieldName = field[j].getName();
                    if (fieldName.equals("c" + (i + 1))) {
                        field[j].setAccessible(true);
                        Object newObj = field[j].get(obj);
                        if (!com.toeic.online.commons.DataUtil.isNullOrEmpty(newObj)) {
                            cell.setCellValue(newObj.toString());
                        }
                        cell.setCellStyle(styleDetail);
                    }
                }
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        my_workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
