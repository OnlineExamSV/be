package com.toeic.online.commons;

import com.toeic.online.constant.AppConstants;
import com.toeic.online.service.dto.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
public class FileExportUtil {

    private static final short BLUE = 4;

    @Resource
    private Properties messageProperties;

    private static final int ONE_THOUSAND = 1000;

    private final Logger log = LoggerFactory.getLogger(FileExportUtil.class);

    /**
     * Write excel normal arg[0] - sheet name Export excel by config customer
     *
     * @param sheetConfigList List<SheetConfigDto>
     * @return byte[]
     * @throws IOException
     */
    @SuppressWarnings({ "rawtypes", "resource" })
    public byte[] exportXLSX(Boolean isSample, List<SheetConfigDTO> sheetConfigList, String title) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        //		XSSFWorkbook workbook = new SXSSFWorkbook(wbTemp, ONE_THOUSAND);
        XSSFSheet sheet = null;
        XSSFFont fontTimeNewRoman = (XSSFFont) workbook.createFont();
        fontTimeNewRoman.setFontName("Times New Roman");
        fontTimeNewRoman.setFontHeightInPoints(new Short("12"));

        DataFormat format = workbook.createDataFormat();

        CellStyle styleLeft = workbook.createCellStyle(); // Create new style
        styleLeft.setVerticalAlignment(VerticalAlignment.TOP);
        styleLeft.setAlignment(HorizontalAlignment.LEFT);
        styleLeft.setWrapText(true);
        styleLeft.setFont(fontTimeNewRoman);
        styleLeft.setDataFormat(format.getFormat("@"));

        CellStyle styleRightNumber = workbook.createCellStyle(); // Create new style
        styleRightNumber.setVerticalAlignment(VerticalAlignment.TOP);
        styleRightNumber.setAlignment(HorizontalAlignment.RIGHT);
        styleRightNumber.setDataFormat(format.getFormat("###,##0"));

        CellStyle styleRightDouble = workbook.createCellStyle(); // Create new style
        styleRightDouble.setVerticalAlignment(VerticalAlignment.TOP);
        styleRightDouble.setAlignment(HorizontalAlignment.RIGHT);
        styleRightDouble.setDataFormat(format.getFormat("###,##0.0#"));
        styleRightDouble.setFont(fontTimeNewRoman);

        CellStyle styleCenterNo = workbook.createCellStyle(); // Create new style
        styleCenterNo.setVerticalAlignment(VerticalAlignment.TOP);
        styleCenterNo.setAlignment(HorizontalAlignment.CENTER);
        styleCenterNo.setDataFormat(format.getFormat("###,##0"));

        CellStyle styleRight = workbook.createCellStyle();
        styleRight.setAlignment(HorizontalAlignment.RIGHT);
        styleRight.setFont(fontTimeNewRoman);

        CellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleTitle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREEN.getIndex());
        XSSFFont fontTitle = (XSSFFont) workbook.createFont();
        fontTitle.setBold(true);
        fontTitle.setFontName("Times New Roman");
        fontTitle.setFontHeightInPoints(new Short("12"));
        fontTitle.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        styleTitle.setFont(fontTitle);

        CellStyle styleError = workbook.createCellStyle();
        styleError.setAlignment(HorizontalAlignment.LEFT);
        XSSFFont fontError = (XSSFFont) workbook.createFont();
        fontError.setColor(IndexedColors.RED1.getIndex());
        fontError.setFontName("Times New Roman");
        fontError.setFontHeightInPoints(new Short("12"));
        styleError.setFont(fontError);
        styleError.setWrapText(true);

        CellStyle styleBorderError = workbook.createCellStyle();
        styleBorderError.setAlignment(HorizontalAlignment.LEFT);
        XSSFFont fontBorderError = (XSSFFont) workbook.createFont();
        styleBorderError.setFont(fontBorderError);
        setBorderError(styleBorderError);

        CellStyle styleBorderErrorNo = workbook.createCellStyle();
        styleBorderErrorNo.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont fontBorderErrorNo = (XSSFFont) workbook.createFont();
        styleBorderErrorNo.setFont(fontBorderErrorNo);
        setBorderError(styleBorderErrorNo);

        CellStyle hyperLinkStyle = workbook.createCellStyle();
        XSSFFont hyperLinkFont = (XSSFFont) workbook.createFont();
        hyperLinkFont.setUnderline(XSSFFont.U_SINGLE);
        hyperLinkFont.setColor(BLUE);
        hyperLinkStyle.setFont(hyperLinkFont);

        CreationHelper createHelper = workbook.getCreationHelper();
        XSSFHyperlink emailHyperLink = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.EMAIL);
        DataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);

        for (SheetConfigDTO sheetConfig : sheetConfigList) {
            if (StringUtils.isNotNullOrEmpty(sheetConfig.getSheetName())) {
                String sheetName = Translator.toLocale(sheetConfig.getSheetName());
                if (StringUtils.isNotNullOrEmpty(sheetName)) {
                    sheet = workbook.createSheet(sheetName);
                } else {
                    sheet = workbook.createSheet(sheetConfig.getSheetName());
                }
            } else {
                sheet = workbook.createSheet(String.format("Sheet%d", sheetConfigList.indexOf(sheetConfig) + 1));
            }

            if (sheetConfig.isHasBorder()) {
                addBorder(styleLeft);
                addBorder(styleRightNumber);
                addBorder(styleRightDouble);
                addBorder(styleRight);
                addBorder(hyperLinkStyle);
                addBorder(styleError);
                addBorder(styleCenterNo);
            } else {
                removeBorder(styleLeft);
                removeBorder(styleRightNumber);
                removeBorder(styleRightDouble);
                removeBorder(styleRight);
                removeBorder(hyperLinkStyle);
                addBorder(styleCenterNo);
            }

            int rowStart = sheetConfig.getRowStart();
            int cellStart = 0;

            XSSFRow row;
            XSSFCell cell;
            if (StringUtils.isNotNullOrEmpty(title)) {
                String titleTrans = Translator.toLocale(title);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, sheetConfig.getHeaders().length - 1));
                row = sheet.createRow(rowStart++);
                cell = row.createCell(0);
                if (StringUtils.isNotNullOrEmpty(titleTrans)) {
                    cell.setCellValue(titleTrans);
                } else {
                    cell.setCellValue(title);
                }
                cell.setCellStyle(styleTitle);
                rowStart = 2;
            }

            List list = sheetConfig.getList();
            String[] headers = sheetConfig.getHeaders();
            boolean hasIndex = sheetConfig.isHasIndex();
            if (headers != null) {
                row = sheet.createRow(rowStart++);
                // write header
                CellStyle styleHeader = workbook.createCellStyle();
                if (sheetConfig.getExportType() == AppConstants.EXPORT_DATA) {
                    styleHeader.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                    styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                styleHeader.setAlignment(HorizontalAlignment.CENTER);
                styleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
                styleHeader.setWrapText(true);
                styleHeader.setFont(fontTimeNewRoman);
                addBorder(styleHeader);

                XSSFFont fontHeaderBold = (XSSFFont) workbook.createFont();
                fontHeaderBold.setBold(true);
                fontHeaderBold.setFontName("Times New Roman");
                fontHeaderBold.setFontHeightInPoints(new Short("12"));

                XSSFFont fontHeaderBoldRed = (XSSFFont) workbook.createFont();
                fontHeaderBoldRed.setBold(true);
                fontHeaderBoldRed.setFontName("Times New Roman");
                fontHeaderBoldRed.setFontHeightInPoints(new Short("12"));
                fontHeaderBoldRed.setColor(Font.COLOR_RED);
                if (hasIndex) {
                    cell = row.createCell(cellStart++);
                    cell.setCellValue(Translator.toLocale("recordNo"));
                    cell.setCellStyle(styleHeader);
                }
                for (String header : headers) {
                    XSSFRichTextString richTextString = new XSSFRichTextString();
                    cell = row.createCell(cellStart++);
                    String headerStr = Translator.toLocale(header);
                    String[] arrHeader = headerStr.split(Pattern.quote(AppConstants.NEXT_LINE));
                    if (arrHeader.length > 0) {
                        if (arrHeader[0].contains(AppConstants.CHAR_STAR)) {
                            String[] arrStar = arrHeader[0].split(Pattern.quote(AppConstants.CHAR_STAR));
                            richTextString.append(arrStar[0], fontHeaderBold);
                            richTextString.append(AppConstants.CHAR_STAR, fontHeaderBoldRed);
                            richTextString.append(arrStar[1], fontHeaderBold);
                            richTextString.append(AppConstants.NEXT_LINE);
                        } else {
                            richTextString.append(arrHeader[0], fontHeaderBold);
                            richTextString.append(AppConstants.NEXT_LINE);
                        }
                        for (int i = 1; i < arrHeader.length; i++) {
                            richTextString.append(arrHeader[i], fontTimeNewRoman);
                            richTextString.append(AppConstants.NEXT_LINE);
                        }
                    } else {
                        richTextString.append(headerStr, fontHeaderBold);
                    }
                    cell.setCellValue(richTextString);
                    cell.setCellStyle(styleHeader);
                }
            }
            List<CellConfigDTO> cellConfigList = sheetConfig.getCellConfigList();
            // write content
            for (Object object : list) {
                List<String> listFieldErr = Arrays.asList(BeanUtils.getArrayProperty(object, "fieldErr"));
                row = sheet.createRow(rowStart++);
                cellStart = 0;
                if (hasIndex) {
                    cell = row.createCell(cellStart++);
                    cell.setCellValue(list.indexOf(object) + 1);
                    cell.setCellStyle(styleRight);
                }
                for (CellConfigDTO cellConfig : cellConfigList) {
                    cell = row.createCell(cellStart++);
                    try {
                        String cellValue = BeanUtils.getProperty(object, cellConfig.getFieldName());
                        String cellValueStr = com.toeic.online.commons.StringUtils.ifNullToEmpty(cellValue);

                        boolean isHyperLinkEmail = cellConfig.isHyperLinkEmail();
                        if (!StringUtils.isNotNullOrEmpty(cellValueStr) || AppConstants.STRING.equals(cellConfig.getCellType())) {
                            cell.setCellValue(StringUtils.ifNullToEmpty(cellValue));
                            if (isHyperLinkEmail) {
                                try {
                                    emailHyperLink.setAddress(
                                        String.format("mailto:%s?subject=Hyperlink", StringUtils.ifNullToEmpty(cellValue))
                                    );
                                    cell.setHyperlink(emailHyperLink);
                                } catch (IllegalArgumentException illegalArgumentE) {
                                    isHyperLinkEmail = false;
                                }
                            }
                        } else if (AppConstants.NUMBER.equals(cellConfig.getCellType())) {
                            cell.setCellValue(Long.valueOf(cellValueStr));
                        } else if (AppConstants.DOUBLE.equals(cellConfig.getCellType())) {
                            cell.setCellValue(Double.valueOf(cellValueStr));
                        } else if (AppConstants.ERRORS.equals(cellConfig.getCellType())) {
                            cell.setCellValue(cellValueStr);
                            cell.setCellStyle(styleError);
                        } else if (AppConstants.NO.equals(cellConfig.getCellType())) {
                            cell.setCellValue(Long.valueOf(cellValueStr));
                        }

                        if (!AppConstants.ERRORS.equals(cellConfig.getCellType())) {
                            String textAlight = StringUtils.ifNullToEmpty(cellConfig.getTextAlight());
                            switch (textAlight) {
                                case AppConstants.ALIGN_RIGHT:
                                    if (!cellConfig.isFormatNumber()) {
                                        if (listFieldErr.contains(cellConfig.getFieldName())) {
                                            cell.setCellStyle(styleBorderError);
                                        } else {
                                            cell.setCellStyle(styleRight);
                                        }
                                    } else if (AppConstants.NUMBER.equals(cellConfig.getCellType())) {
                                        if (listFieldErr.contains(cellConfig.getFieldName())) {
                                            cell.setCellStyle(styleBorderError);
                                        } else {
                                            cell.setCellStyle(styleRightNumber);
                                        }
                                    } else if (AppConstants.DOUBLE.equals(cellConfig.getCellType())) {
                                        if (listFieldErr.contains(cellConfig.getFieldName())) {
                                            cell.setCellStyle(styleBorderError);
                                        } else {
                                            cell.setCellStyle(styleRightDouble);
                                        }
                                    } else if (isHyperLinkEmail) {
                                        if (listFieldErr.contains(cellConfig.getFieldName())) {
                                            cell.setCellStyle(styleBorderError);
                                        } else {
                                            cell.setCellStyle(hyperLinkStyle);
                                        }
                                    } else if (AppConstants.NO.equals(cellConfig.getCellType())) {
                                        if (listFieldErr.contains(cellConfig.getFieldName())) {
                                            cell.setCellStyle(styleBorderErrorNo);
                                        } else {
                                            cell.setCellStyle(styleCenterNo);
                                        }
                                    }
                                    break;
                                default:
                                    if (listFieldErr.contains(cellConfig.getFieldName())) {
                                        cell.setCellStyle(styleBorderError);
                                    } else {
                                        cell.setCellStyle(styleLeft);
                                    }
                                    break;
                            }
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new Exception("Exception occurred when get value by propertiy", e);
                    }
                }
            }

            List<CellConfigDTO> cellCustomList = sheetConfig.getCellCustomList();
            if (StringUtils.isListNotNullOrEmpty(cellCustomList)) {
                int countSheet = 1;
                for (CellConfigDTO i : cellCustomList) {
                    DataValidation validation = creatDropDownList(sheet, dvHelper, workbook, i);
                    sheet.addValidationData(validation);
                    workbook.setSheetHidden(countSheet++, true);
                }
            }
            for (int i = 0; i < sheetConfig.getCellConfigList().size(); i++) {
                sheet.autoSizeColumn(i);
            }
        }

        if (!isSample) {
            Row r = sheet.getRow(2);
            ExportUtils ex = new ExportUtils();
            for (Cell c : r) {
                c.setCellStyle(ex.createStyleHeader(workbook));
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    private void setBorderError(CellStyle style) {
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setLeftBorderColor(IndexedColors.RED.getIndex());
        style.setRightBorderColor(IndexedColors.RED.getIndex());
        style.setTopBorderColor(IndexedColors.RED.getIndex());
        style.setBottomBorderColor(IndexedColors.RED.getIndex());
    }

    private static DataValidation creatDropDownList(
        XSSFSheet taskInfoSheet,
        DataValidationHelper helper,
        XSSFWorkbook book,
        CellConfigDTO dto
    ) {
        XSSFSheet hidden = book.createSheet("hidden" + dto.getFieldName());
        Cell cell = null;
        for (int i = 0, length = dto.getArrData().length; i < length; i++) {
            String name = dto.getArrData()[i];
            Row row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(name);
        }

        Name namedCell = book.createName();
        namedCell.setNameName("hidden" + dto.getFieldName());
        namedCell.setRefersToFormula("hidden" + dto.getFieldName() + "!$A$1:$A$" + dto.getArrData().length);
        DataValidationConstraint constraint = helper.createFormulaListConstraint("hidden" + dto.getFieldName());
        CellRangeAddressList addressList = new CellRangeAddressList(
            dto.getFirstRow(),
            dto.getLastRow(),
            dto.getFirstCol(),
            dto.getLastCol()
        );
        DataValidation validation = helper.createValidation(constraint, addressList);
        // The second sheet is set to hide
        if (null != validation) {
            taskInfoSheet.addValidationData(validation);
        }
        return validation;
    }

    /**
     * Write excel normal
     *
     * @param list       List<?>
     * @param headers    String[]
     * @param properties String[]
     * @return File
     */
    @SuppressWarnings("resource")
    public byte[] exportXLSX(List<?> list, final String[] headers, final String[] properties, String... params) throws Exception {
        String sheetName = "Data";
        XSSFWorkbook wbTemp = new XSSFWorkbook();
        SXSSFWorkbook workbook = new SXSSFWorkbook(wbTemp, 1000);
        if (params.length > 0 && StringUtils.isNotNullOrEmpty(params[0])) {
            sheetName = params[0];
        }
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        sheet.trackAllColumnsForAutoSizing();
        int rowStart = 0;
        int cellStart = 0;
        SXSSFRow row = sheet.createRow(rowStart++);
        SXSSFCell cell;

        CellStyle style = workbook.createCellStyle(); // Create new style
        style.setVerticalAlignment(VerticalAlignment.TOP);

        // write header
        if (params.length > 1 && StringUtils.isNotNullOrEmpty(params[1])) {
            int lengthOfAHeader = Integer.parseInt(params[1]);
            int cells = 0;
            while (cells < headers.length - lengthOfAHeader) {
                cell = row.createCell(cellStart++);
                cell.setCellValue(messageProperties.getProperty(headers[cells++]));
                if (cellStart == lengthOfAHeader) {
                    cellStart = 0;
                    row = sheet.createRow(rowStart++);
                }
            }
            for (int index = headers.length - lengthOfAHeader; index < headers.length; index++) {
                cell = row.createCell(cellStart++);
                cell.setCellValue(messageProperties.getProperty(headers[cells++]));
            }
        } else {
            for (String header : headers) {
                cell = row.createCell(cellStart++);
                cell.setCellValue(messageProperties.getProperty(header));
            }
        }

        // write content
        for (Object object : list) {
            row = sheet.createRow(rowStart++);
            cellStart = 0;
            for (String property : properties) {
                cell = row.createCell(cellStart++);
                try {
                    String cellValue = BeanUtils.getProperty(object, property);
                    cell.setCellValue(StringUtils.ifNullToEmpty(cellValue));
                    cell.setCellStyle(style);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException reflectiveOperationE) {
                    throw new Exception("Exception occurred when get value by propertiy");
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException ioE) {
            throw new Exception("An exception occurred when export excel");
        }
    }

    /**
     * Create file name for convention
     *
     * @param module   String: screen code
     * @param memberId String: user export
     * @param fileType String eg(xlxs,doc,pdf...)
     * @return String file name standard
     */
    public String getFileName(String module, String exportType, String memberId, String fileType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(AppConstants.YYYYMMDD);
        String strCurTimeExp = dateFormat.format(new Date());
        exportType = exportType.length() == 1 ? ("0" + exportType) : exportType;
        return new StringBuilder()
            .append(module)
            .append("_")
            .append(exportType)
            .append("_")
            .append(strCurTimeExp)
            .append("_(")
            .append(memberId)
            .append(").")
            .append(fileType)
            .toString();
    }

    /**
     * Create file name for convention
     *
     * @param module    String: screen code
     * @param extension String eg(xlxs,doc,pdf...)
     * @return String file name standard
     */
    public String getFileName(String module, String extension) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.applyPattern(AppConstants.YYYYMMDDHHSS);
        String strCurTimeExp = dateFormat.format(new Date());
        return String.format("%s_%s.%s", module, strCurTimeExp, extension);
    }

    /**
     * File to stream
     *
     * @param file     File
     * @param module   String: screen code
     * @param type     String: export type in screen
     * @param memberId String: user export
     * @param fileType String ex(slxs,doc,pdf...)
     * @return HttpEntity<byte [ ]>
     */
    public HttpEntity<byte[]> fileToStream(File file, String module, String type, String memberId, String fileType) throws Exception {
        HttpHeaders header = new HttpHeaders();
        try {
            // create file name
            String fileName = getFileName(module, type, memberId, fileType);
            // get file data
            byte[] fileData = FileCopyUtils.copyToByteArray(file);
            // create new media type
            MediaType mediaType = new MediaType("application", fileType);
            header.setContentType(mediaType);
            header.set("Content-Disposition", "inline; filename=" + fileName);
            header.setContentLength(fileData.length);
            // delete file
            file.deleteOnExit();
            return new HttpEntity<byte[]>(fileData, header);
        } catch (IOException ioE) {
            throw new Exception("Error When Export file in screen Horse Status Report: " + ioE.getMessage(), ioE);
        }
    }

    /**
     * Response a file to client arg[0] - add hour into file name export (arg[0] = noAddHour --> no add hour into file
     * name)
     *
     * @param response      HttpServletResponse
     * @param fileData      byte[]
     * @param functionId    String
     * @param fileExtension String
     * @param contentType   String(MIME_TYPE of file)
     */
    public void responseFileExport(
        HttpServletResponse response,
        byte[] fileData,
        String functionId,
        String fileExtension,
        String contentType
    ) throws Exception {
        try {
            String fileName = getFileName(functionId, fileExtension);
            response.setContentType(contentType);
            response.setHeader(AppConstants.CONTENT_DISPOSITION, String.format(AppConstants.ATTACHMENT_FILENAME, fileName));
            response.setHeader(AppConstants.CONTENT_LENGTH, String.valueOf(fileData.length));
            OutputStream out = response.getOutputStream();
            out.write(fileData);
            out.flush();
        } catch (IOException ioE) {
            throw new Exception("An exception occured when reponse file to client", ioE);
        }
    }

    /**
     * Set border for cell type
     *
     * @param style
     */
    private void addBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
    }

    /**
     * Set border for cell type none
     *
     * @param style
     */
    private void removeBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.NONE);
        style.setBorderTop(BorderStyle.NONE);
        style.setBorderRight(BorderStyle.NONE);
        style.setBorderLeft(BorderStyle.NONE);
    }

    public ResponseEntity<?> responseFileExportWithUtf8FileName(byte[] fileData, String fileName, String contentType) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
        InputStreamResource resource = new InputStreamResource(inputStream);
        try {
            fileName = URLEncoder.encode(fileName, AppConstants.Encoding_UTF8);
        } catch (IOException ioE) {
            throw new Exception("An exception occured when reponse file to client", ioE);
        }
        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(contentType))
            .contentLength(inputStream.available())
            .header("filename", fileName)
            .body(resource);
    }

    private Map<String, String> getMapDateAndCheckDateDefault(int year, int month) {
        Map<String, String> map = new HashMap<>();
        // 31 1-3-5-7-8-10-12
        if (month == 1 | month == 3 | month == 5 | month == 7 | month == 8 | month == 10 | month == 12) {
            for (int i = 1; i <= 31; i++) {
                map.put(String.valueOf(i), "-");
            }
        } else if (month == 4 | month == 6 | month == 9 | month == 11) {
            for (int i = 1; i <= 30; i++) {
                map.put(String.valueOf(i), "-");
            }
        } else {
            if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                for (int i = 1; i <= 29; i++) {
                    map.put(String.valueOf(i), "-");
                }
            } else {
                for (int i = 1; i <= 28; i++) {
                    map.put(String.valueOf(i), "-");
                }
            }
        }
        return map;
    }

    private Map<String, String> copyMap(Map<String, String> map1, Map<String, String> map2) {
        for (String key1 : map1.keySet()) {
            if (map2 == null) {
                return map1;
            }
            for (String key2 : map2.keySet()) {
                if (key1.equals(key2)) {
                    map1.put(key1, map2.get(key2));
                    break;
                }
            }
        }
        return map1;
    }

    // dem so ngay thu 7 va chu nhat trong thang
    private int countSaturdayAndSunday(int numberDay, int month, int currentYear) {
        int result = 0;
        for (int i = 1; i <= numberDay; i++) {
            switch (LocalDate.of(currentYear, month, i).getDayOfWeek()) {
                case SATURDAY:
                case SUNDAY:
                    {
                        result += 1;
                        break;
                    }
                default:
                    {
                        result += 0;
                    }
            }
        }
        return result;
    }

    private int totalDayInWeek(LocalDate laLocalDate, boolean moreDay, List<String> lstHoliday) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int total = 0;
        int totalMinus = 0;
        int month = laLocalDate.getMonthValue();
        int year = laLocalDate.getYear();

        if (moreDay) {
            for (int i = 1; i < laLocalDate.getDayOfMonth(); i++) {
                boolean tag = false;
                for (String day : lstHoliday) {
                    try {
                        if (LocalDate.parse(day, formatter).equals(LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i))) {
                            tag = true;
                            break;
                        }
                    } catch (Exception e) {}
                }
                if (tag) {
                    continue;
                }
                switch (LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i).getDayOfWeek()) {
                    case MONDAY:
                    case TUESDAY:
                    case WEDNESDAY:
                    case THURSDAY:
                    case FRIDAY:
                        {
                            total += 1;
                            break;
                        }
                    case SATURDAY:
                    case SUNDAY:
                        {
                            totalMinus += 1;
                            break;
                        }
                }
            }
        } else {
            if (month == 1 | month == 3 | month == 5 | month == 7 | month == 8 | month == 10 | month == 12) {
                for (int i = laLocalDate.getDayOfMonth(); i <= 31; i++) {
                    boolean tag = false;
                    for (String day : lstHoliday) {
                        try {
                            if (
                                LocalDate.parse(day, formatter).equals(LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i))
                            ) {
                                tag = true;
                                break;
                            }
                        } catch (Exception e) {}
                    }
                    if (tag) {
                        continue;
                    }
                    switch (LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i).getDayOfWeek()) {
                        case MONDAY:
                        case TUESDAY:
                        case WEDNESDAY:
                        case THURSDAY:
                        case FRIDAY:
                            {
                                total += 1;
                                break;
                            }
                        default:
                            {
                                break;
                            }
                    }
                }
            } else if (month == 4 | month == 6 | month == 9 | month == 11) {
                for (int i = laLocalDate.getDayOfMonth(); i <= 30; i++) {
                    boolean tag = false;
                    for (String day : lstHoliday) {
                        try {
                            if (
                                LocalDate.parse(day, formatter).equals(LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i))
                            ) {
                                tag = true;
                                break;
                            }
                        } catch (Exception e) {}
                    }
                    if (tag) {
                        continue;
                    }
                    switch (LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i).getDayOfWeek()) {
                        case MONDAY:
                        case TUESDAY:
                        case WEDNESDAY:
                        case THURSDAY:
                        case FRIDAY:
                            {
                                total += 1;
                                break;
                            }
                        default:
                            {
                                break;
                            }
                    }
                }
            } else {
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    for (int i = laLocalDate.getDayOfMonth(); i <= 29; i++) {
                        boolean tag = false;
                        for (String day : lstHoliday) {
                            try {
                                if (
                                    LocalDate
                                        .parse(day, formatter)
                                        .equals(LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i))
                                ) {
                                    tag = true;
                                    break;
                                }
                            } catch (Exception e) {}
                        }
                        if (tag) {
                            continue;
                        }
                        switch (LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i).getDayOfWeek()) {
                            case MONDAY:
                            case TUESDAY:
                            case WEDNESDAY:
                            case THURSDAY:
                            case FRIDAY:
                            default:
                                {
                                    break;
                                }
                        }
                    }

                    for (int i = laLocalDate.getDayOfMonth(); i <= 28; i++) {
                        boolean tag = false;
                        for (String day : lstHoliday) {
                            try {
                                if (
                                    LocalDate
                                        .parse(day, formatter)
                                        .equals(LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i))
                                ) {
                                    tag = true;
                                    break;
                                }
                            } catch (Exception e) {}
                        }
                        if (tag) {
                            continue;
                        }
                        switch (LocalDate.of(laLocalDate.getYear(), laLocalDate.getMonthValue(), i).getDayOfWeek()) {
                            case MONDAY:
                            case TUESDAY:
                            case WEDNESDAY:
                            case THURSDAY:
                            case FRIDAY:
                                {
                                    total += 1;
                                    break;
                                }
                            default:
                                {
                                    break;
                                }
                        }
                    }
                }
            }
        }

        return (total);
    }
}
