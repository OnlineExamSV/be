package com.toeic.online.service.impl;

import com.google.common.collect.Lists;
import com.toeic.online.commons.*;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Classroom;
import com.toeic.online.domain.Teacher;
import com.toeic.online.domain.User;
import com.toeic.online.repository.ClassroomRepository;
import com.toeic.online.repository.ClassroomRepositoryCustom;
import com.toeic.online.repository.TeacherRepository;
import com.toeic.online.service.ClassroomService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {

    private final Logger log = LoggerFactory.getLogger(ClassroomServiceImpl.class);

    private final ClassroomRepositoryCustom classroomRepositoryCustom;

    private final ClassroomRepository classroomRepository;

    private final TeacherRepository teacherRepository;

    private final ExportUtils exportUtils;

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    List<ExcelDynamicDTO> lstError;

    @Value("${import-file.sample-file}")
    private String folderSampleFile;

    private static final String SHEETNAME = "classroom.sheetName";

    public ClassroomServiceImpl(
        ClassroomRepositoryCustom classroomRepositoryCustom,
        ClassroomRepository classroomRepository,
        TeacherRepository teacherRepository,
        ExportUtils exportUtils,
        FileExportUtil fileExportUtil,
        UserService userService
    ) {
        this.classroomRepositoryCustom = classroomRepositoryCustom;
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.exportUtils = exportUtils;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
    }

    @Override
    public List<ClassroomDTO> exportData(ClassroomSearchDTO classroomSearchDTO) {
        return classroomRepositoryCustom.exportData(classroomSearchDTO);
    }

    @Override
    public ByteArrayInputStream getSampleFile() throws IOException {
        List<Teacher> lstTeacher = teacherRepository.findAll();
        List<String> array1 = new ArrayList<>();

        for (Teacher teacher : lstTeacher) {
            array1.add(teacher.getCode());
        }

        String path = folderSampleFile + "DS_Lophoc.xls";
        InputStream file = new BufferedInputStream(new FileInputStream(path));

        HSSFWorkbook wb = new HSSFWorkbook(file);
        HSSFSheet sheet = wb.getSheetAt(0);
        if (!array1.isEmpty()) {
            HSSFDataValidation dataValidation2 = creatDropDownList(sheet, array1.toArray(new String[0]), 1, 500, 3, 3, wb, "hidden");
            sheet.addValidationData(dataValidation2);
            wb.setSheetHidden(wb.getSheetIndex("hidden"), true);
        }

        Font fontTimeNewRoman = exportUtils.getFontTimeNewRoman(wb);
        Font fontHeaderBold = exportUtils.getFontHeaderBold(wb);
        Font fontHeaderBoldRed = exportUtils.getFontHeaderBoldRed(wb);
        CellStyle styleHeader = exportUtils.getStyleHeader(wb, fontTimeNewRoman);

        Row rowHeader = sheet.getRow(0);
        for (int i = 0; i < 4; i++) {
            Cell cell = rowHeader.getCell(i);
            String headerStr = "";
            switch (i) {
                case 0:
                    headerStr = ("STT");
                    break;
                case 1:
                    headerStr = ("Mã lớp");
                    break;
                case 2:
                    headerStr = ("Tên lớp");
                    break;
                case 3:
                    headerStr = ("Mã giáo viên");
                    break;
            }
            if (i == 0) {
                cell.setCellValue(headerStr);
                cell.setCellStyle(styleHeader);
                continue;
            }
            this.buildHeaderFileTemplate(headerStr, cell, fontHeaderBoldRed, fontHeaderBold, fontTimeNewRoman, styleHeader);
        }

        file.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public Map<String, Object> search(ClassroomSearchDTO classroomSearchDTO, Integer page, Integer pageSize) {
        List<ClassroomDTO> lstClass = classroomRepositoryCustom.search(classroomSearchDTO, page, pageSize);
        Integer total = classroomRepositoryCustom.exportData(classroomSearchDTO).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstClassroom", lstClass);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public List<ClassroomDTO> importClassRoom(MultipartFile file, String fileInputPath, Integer isAddNew, String year) throws Exception {
        List<ClassroomDTO> result = Lists.newArrayList();
        List<Integer> listRowSuccess = new ArrayList<>();
        lstError = new ArrayList<>();
        Long countSuccess = 0L;
        Long totalItem = 0L;
        ExcelDynamicDTO errorFormat = null;
        String fileName = file.getOriginalFilename();
        int validFile = FileUtils.validateAttachFileExcel(file, fileName);
        if (validFile != 0) {
            log.error("The specified file is not Excel file");
            return result;
        }

        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        DataFormatter formatter = new DataFormatter();
        int count = 0;
        for (Row row : sheet) {
            count++;
            //Check format file
            if (count == 1 && (row.getLastCellNum() < 7 || row.getLastCellNum() > 8)) return result;

            if (count < 2 || (null != row && null == row.getCell(0))) continue;
            totalItem++;
            boolean checkColumn1 = true;
            boolean checkColumn2 = true;
            boolean checkColumn3 = true;
            String gradeLevelCode;
            String deptCode = "";
            String roomCode = "";
            String roomName = "";
            String teacherCode = "";
            String specializeCode = "";

            boolean checkRowIsNull = true;
            for (Cell cell : row) {
                checkRowIsNull = false;
                errorFormat = new ExcelDynamicDTO();
                if (cell.getColumnIndex() == 1) {
                    gradeLevelCode = formatter.formatCellValue(cell);
                    checkColumn1 = checkDataFromFileExel(gradeLevelCode, count, cell.getColumnIndex(), errorFormat, isAddNew);
                }
                if (cell.getColumnIndex() == 2) {
                    deptCode = formatter.formatCellValue(cell);
                    checkColumn2 = checkDataFromFileExel(deptCode, count, cell.getColumnIndex(), errorFormat, isAddNew);
                }
                if (cell.getColumnIndex() == 3) {
                    roomCode = formatter.formatCellValue(cell);
                    checkColumn3 = checkDataFromFileExel(roomCode, count, cell.getColumnIndex(), errorFormat, isAddNew);
                }
            }
            if (checkRowIsNull) totalItem--;
            if (checkColumn1 && checkColumn2 && checkColumn3 && !checkRowIsNull) {
                ClassroomDTO newClass = new ClassroomDTO();
                DataFormatter df = new DataFormatter();
                String code = df.formatCellValue(row.getCell(3));
                newClass.setCode(code.trim());
                String name = df.formatCellValue(row.getCell(4));
                newClass.setName(name.trim());
                newClass.setTeacherCode(row.getCell(5).getStringCellValue().trim());
                // Check update or create
                // Save or update
                if (isAddNew.equals(0)) {
                    log.info("Them moi=====================================>");
                } else {
                    log.info("Update lai ==================================>");
                }
                countSuccess++;
                listRowSuccess.add(row.getRowNum());
                result.add(newClass);
            }
        }
        return null;
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<ClassroomDTO> lstClassroomDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstClassroomDTO, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceResult<?> importClassroom(MultipartFile fileUploads, Long typeImport) throws Exception {
        ResponseImportDTO importDTO = new ResponseImportDTO();
        if (!FileUploadUtil.isNotNullOrEmpty(fileUploads)) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, "File không tồn tại");
        //check extension file
        String extention = FilenameUtils.getExtension(fileUploads.getOriginalFilename());
        if (
            !AppConstants.EXTENSION_XLSX.equals(extention.toLowerCase()) && !AppConstants.EXTENSION_XLS.equals(extention.toLowerCase())
        ) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.format"));

        List<List<String>> records;
        try {
            records = FileUploadUtil.excelReader(fileUploads);
        } catch (IllegalStateException | IOException e) {
            return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.read"));
        }

        if (records.size() <= 1) return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.no.data"));

        //remove record header
        records.remove(0);

        List<Classroom> dataSuccess = new ArrayList<>();
        List<ClassroomDTO> dataErrors = new ArrayList<>();
        List<Teacher> lstTeacher = teacherRepository.findAll();
        List<Classroom> lstClassroom = classroomRepository.findAll();
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 4) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            ClassroomDTO dto = processRecord(record, lstTeacher, lstClassroom, typeImport);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                Classroom classroom = new Classroom(dto);
                dataSuccess.add(classroom);
                classroom = classroomRepository.save(classroom);
                lstClassroom.add(classroom);
                dto.setId(classroom.getId());
                countSuccess++;
            }
            total++;
        }

        ResponseImportDTO responseImportDTO = new ResponseImportDTO(countError, countSuccess, total);
        responseImportDTO.setListErrors(dataErrors);
        responseImportDTO.setListSuccess(dataSuccess);
        return new ServiceResult<>(responseImportDTO, HttpStatus.OK, Translator.toLocale("msg.import.success"));
    }

    @Override
    public byte[] exportExcelClassroomErrors(List<ClassroomDTO> listDataErrors) throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, listDataErrors, lstSheetConfigDTO, AppConstants.EXPORT_ERRORS);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    public boolean checkDataFromFileExel(String data, int rowIndex, int columnIndex, ExcelDynamicDTO errorFormat, Integer isAddNew) {
        if (columnIndex == 1) {
            if (StringUtils.isBlank(data)) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Mã lớp học không được để trống");
                lstError.add(errorFormat);
                return false;
            }
            String dataTrim = data.trim();
            if (dataTrim.length() > 50) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Mã lớp học có dài không có 50 ký tự");
                lstError.add(errorFormat);
                return false;
            }
            List<Classroom> lst = classroomRepository.findClassCode(dataTrim);
            if (isAddNew.equals(0)) {
                if (!lst.isEmpty()) {
                    errorFormat.setLineError(String.valueOf(rowIndex));
                    errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                    errorFormat.setDetailError("Mã lớp học đã tồn tại");
                    lstError.add(errorFormat);
                    return false;
                }
            }
            if (isAddNew.equals(1)) {
                if (lst.isEmpty()) {
                    errorFormat.setLineError(String.valueOf(rowIndex));
                    errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                    errorFormat.setDetailError("Mã lớp học đã tồn tại");
                    lstError.add(errorFormat);
                    return false;
                }
            }
        }
        if (columnIndex == 2) {
            if (StringUtils.isBlank(data)) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Tên lớp học không được để trống");
                lstError.add(errorFormat);
                return false;
            }
            String dataTrim = data.trim();
            if (dataTrim.length() > 250) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Tên lớp học có dài không có 50 ký tự");
                lstError.add(errorFormat);
                return false;
            }
        }
        if (columnIndex == 3) {
            if (StringUtils.isBlank(data)) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Mã giáo viên không được để trống");
                lstError.add(errorFormat);
                return false;
            }
            String dataTrim = data.trim();
            if (dataTrim.length() > 250) {
                errorFormat.setLineError(String.valueOf(rowIndex));
                errorFormat.setColumnError(String.valueOf(columnIndex + 1));
                errorFormat.setDetailError("Tên lớp học có dài không có 50 ký tự");
                lstError.add(errorFormat);
                return false;
            }
        }
        return true;
    }

    private static HSSFDataValidation creatDropDownList(
        Sheet taskInfoSheet,
        String[] dataArray,
        Integer firstRow,
        Integer lastRow,
        Integer firstCol,
        Integer lastCol,
        Workbook book,
        String sheetName
    ) {
        Sheet hidden = book.createSheet(sheetName);
        Cell cell = null;
        for (int i = 0, length = dataArray.length; i < length; i++) {
            String name = dataArray[i];
            Row row = hidden.createRow(i);
            cell = row.createCell(0);
            cell.setCellValue(name);
        }

        Name namedCell = book.createName();
        namedCell.setNameName(sheetName);
        namedCell.setRefersToFormula(sheetName + "!$A$1:$A$" + dataArray.length);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(sheetName);
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);
        // The second sheet is set to hide
        book.setSheetHidden(1, true);
        if (null != validation) {
            taskInfoSheet.addValidationData(validation);
        }
        return validation;
    }

    private void buildHeaderFileTemplate(
        String headerStr,
        Cell cell,
        Font fontHeaderBoldRed,
        Font fontHeaderBold,
        Font fontTimeNewRoman,
        CellStyle styleHeader
    ) {
        HSSFRichTextString richTextString = new HSSFRichTextString(headerStr);
        String[] arrHeader = headerStr.split(Pattern.quote("\n"));
        if (arrHeader.length > 0) {
            richTextString.applyFont(0, arrHeader[0].length(), fontHeaderBold);
            richTextString.applyFont(arrHeader[0].length(), headerStr.length() - 1, fontTimeNewRoman);
        }
        int startIndex = headerStr.indexOf('*');
        if (startIndex != -1) {
            richTextString.applyFont(startIndex, startIndex + 1, fontHeaderBoldRed);
        }
        cell.setCellValue(richTextString);
        cell.setCellStyle(styleHeader);
    }

    // Dowload file mẫu
    private List<SheetConfigDTO> getDataForExcel(
        String sheetName,
        List<ClassroomDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr = new String[] { "recordNo", "classroom.code", "classroom.name", "teacher.code" };
        } else {
            headerArr = new String[] { "recordNo", "classroom.code", "classroom.name", "teacher.code", "descriptionErrors" };
        }
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            List<Teacher> lstTeacher = teacherRepository.findAll();
            List<String> lstCodeTeacher = lstTeacher.stream().map(Teacher::getCode).collect(Collectors.toList());
            cellConfigCustomList.add(
                new CellConfigDTO("teacherCode", AppConstants.ALIGN_LEFT, lstCodeTeacher.toArray(new String[0]), 1, 99, 3, 3)
            );

            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    ClassroomDTO data = new ClassroomDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (ClassroomDTO item : lstDataSheet) {
                    item.setRecordNo(recordNo++);
                    item.setMessageStr(String.join(AppConstants.NEXT_LINE, item.getMessageErr()));
                }
            }
        }

        sheetConfig.setList(lstDataSheet);
        List<CellConfigDTO> cellConfigList = new ArrayList<>();

        cellConfigList.add(new CellConfigDTO("recordNo", AppConstants.ALIGN_RIGHT, AppConstants.NO));
        cellConfigList.add(new CellConfigDTO("code", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("name", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("teacherCode", AppConstants.ALIGN_LEFT, AppConstants.STRING));

        if (exportType == AppConstants.EXPORT_DATA || exportType == AppConstants.EXPORT_ERRORS) {
            cellConfigList.add(new CellConfigDTO("messageStr", AppConstants.ALIGN_LEFT, AppConstants.ERRORS));
        }

        sheetConfig.setHasIndex(false);
        sheetConfig.setHasBorder(true);
        sheetConfig.setExportType(exportType.intValue());
        sheetConfig.setCellConfigList(cellConfigList);
        sheetConfig.setCellCustomList(cellConfigCustomList);
        lstSheetConfigDTO.add(sheetConfig);
        return lstSheetConfigDTO;
    }

    private ClassroomDTO processRecord(List<String> record, List<Teacher> lstTeacher, List<Classroom> lstClassroom, Long typeImport) {
        ClassroomDTO classroomDTO = new ClassroomDTO();
        List<String> messErr = new ArrayList<>();
        List<String> fieldErr = new ArrayList<>();
        Optional<User> userCreate = userService.getUserWithAuthorities();

        int col = 1;
        if (typeImport == AppConstants.IMPORT_INSERT) {
            classroomDTO.setCreateName(userCreate.get().getLogin());
            classroomDTO.setCreateDate(Instant.now());
        } else {
            classroomDTO.setUpdateName(userCreate.get().getLogin());
            classroomDTO.setUpdateDate(Instant.now());
        }
        String classCode = record.get(col++);
        classroomDTO.setCode(classCode);
        Classroom classroom = lstClassroom.stream().filter(c -> classCode.equalsIgnoreCase(c.getCode())).findAny().orElse(null);
        if (StringUtils.isBlank(classCode)) {
            messErr.add("Mã lớp học không được để trống");
            fieldErr.add("code");
        } else if (classCode.length() < 0 || classCode.length() > 50) {
            messErr.add("Mã lớp học không được quá 50 ký tự");
            fieldErr.add("code");
        } else if (typeImport == AppConstants.IMPORT_UPDATE) {
            if (null != classroom) {
                classroomDTO.setId(classroom.getId());
                classroomDTO.setCreateDate(classroom.getCreateDate());
                classroomDTO.setCreateName(classroom.getCreateName());
            }
        } else {
            if (null != classroom) {
                messErr.add("Mã lớp học đã tồn tại");
                fieldErr.add("code");
            }
        }

        String className = record.get(col++);
        classroomDTO.setName(className);
        if (StringUtils.isBlank(className)) {
            messErr.add("Tên lớp học không được để trống");
            fieldErr.add("name");
        } else if (className.length() < 0 || className.length() > 250) {
            messErr.add("Tên lớp học không được quá 250 ký tự");
            fieldErr.add("name");
        }

        String teacherCode = record.get(col++);
        classroomDTO.setTeacherCode(teacherCode);
        if (StringUtils.isBlank(teacherCode)) {
            messErr.add("Mã giảng viên không được để trống");
            fieldErr.add("teacherCode");
        } else if (classCode.length() < 0 || classCode.length() > 50) {
            messErr.add("Mã giảng viên không được quá 50 ký tự");
            fieldErr.add("teacherCode");
        }
        classroomDTO.setStatus(true);
        classroomDTO.setMessageErr(messErr);
        classroomDTO.setFieldErr(fieldErr);
        return classroomDTO;
    }
}
