package com.toeic.online.service.impl;

import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUploadUtil;
import com.toeic.online.commons.Translator;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.*;
import com.toeic.online.repository.ClassroomStudentRepository;
import com.toeic.online.repository.ClassroomStudentRepositoryCustom;
import com.toeic.online.repository.StudentRepository;
import com.toeic.online.service.ClassroomStudentService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ClassroomStudentServiceImpl implements ClassroomStudentService {

    private final ClassroomStudentRepositoryCustom classroomStudentRepositoryCustom;

    private final StudentRepository studentRepository;

    private final ClassroomStudentRepository classroomStudentRepository;

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    List<ExcelDynamicDTO> lstError;

    private static final String SHEETNAME = "classroom.student.sheetName";

    public ClassroomStudentServiceImpl(
        ClassroomStudentRepositoryCustom classroomStudentRepositoryCustom,
        StudentRepository studentRepository,
        ClassroomStudentRepository classroomStudentRepository,
        FileExportUtil fileExportUtil,
        UserService userService
    ) {
        this.classroomStudentRepositoryCustom = classroomStudentRepositoryCustom;
        this.studentRepository = studentRepository;
        this.classroomStudentRepository = classroomStudentRepository;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
    }

    @Override
    public Map<String, Object> search(String classCode, String studentCode, Integer page, Integer pageSize) {
        List<ClassroomStudentDTO> lstClassroomStudent = classroomStudentRepositoryCustom.search(classCode, studentCode, page, pageSize);
        Integer total = classroomStudentRepositoryCustom.exportData(classCode, studentCode).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstClassroomStudent", lstClassroomStudent);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public List<ClassroomStudentDTO> exportData(String classCode, String studentCode) {
        return classroomStudentRepositoryCustom.exportData(classCode, studentCode);
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<ClassroomStudentDTO> lstClassroomStudent = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstClassroomStudent, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importClassroomStudent(MultipartFile fileUploads, String classCode, Long typeImport) throws Exception {
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

        List<ClassroomStudent> dataSuccess = new ArrayList<>();
        List<ClassroomStudentDTO> dataErrors = new ArrayList<>();
        List<Student> lstStudent = studentRepository.findAll();
        List<ClassroomStudent> lstClassroomStudent = classroomStudentRepository.getListClassroomStudentByClassCode(classCode);
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 2) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            ClassroomStudentDTO dto = processRecord(record, lstStudent, lstClassroomStudent, typeImport);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                ClassroomStudent classroomStudent = new ClassroomStudent(dto);
                classroomStudent.setClassCode(classCode);
                dataSuccess.add(classroomStudent);
                classroomStudent = classroomStudentRepository.save(classroomStudent);
                lstClassroomStudent.add(classroomStudent);
                dto.setId(classroomStudent.getId());
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
    public byte[] exportExcelClassroomErrors(List<ClassroomStudentDTO> listDataErrors) throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, listDataErrors, lstSheetConfigDTO, AppConstants.EXPORT_ERRORS);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    private List<SheetConfigDTO> getDataForExcel(
        String sheetName,
        List<ClassroomStudentDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr = new String[] { "recordNo", "student.code" };
        } else {
            headerArr = new String[] { "recordNo", "student.code", "descriptionErrors" };
        }
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            List<Student> lstStudent = studentRepository.findAll();
            List<String> lstCodeStudent = lstStudent.stream().map(Student::getCode).collect(Collectors.toList());
            cellConfigCustomList.add(
                new CellConfigDTO("studentCode", AppConstants.ALIGN_LEFT, lstCodeStudent.toArray(new String[0]), 1, 99, 1, 3)
            );

            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    ClassroomStudentDTO data = new ClassroomStudentDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (ClassroomStudentDTO item : lstDataSheet) {
                    item.setRecordNo(recordNo++);
                    item.setMessageStr(String.join(AppConstants.NEXT_LINE, item.getMessageErr()));
                }
            }
        }

        sheetConfig.setList(lstDataSheet);
        List<CellConfigDTO> cellConfigList = new ArrayList<>();

        cellConfigList.add(new CellConfigDTO("recordNo", AppConstants.ALIGN_RIGHT, AppConstants.NO));
        cellConfigList.add(new CellConfigDTO("studentCode", AppConstants.ALIGN_LEFT, AppConstants.STRING));

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

    private ClassroomStudentDTO processRecord(
        List<String> record,
        List<Student> lstStudent,
        List<ClassroomStudent> lstClassroomStudent,
        Long typeImport
    ) {
        ClassroomStudentDTO classroomStudentDTO = new ClassroomStudentDTO();
        List<String> messErr = new ArrayList<>();
        List<String> fieldErr = new ArrayList<>();
        int col = 1;
        String studentCode = record.get(col++);
        classroomStudentDTO.setStudentCode(studentCode);
        ClassroomStudent classroomStudent = lstClassroomStudent
            .stream()
            .filter(c -> studentCode.equalsIgnoreCase(c.getStudentCode()))
            .findAny()
            .orElse(null);
        if (StringUtils.isBlank(studentCode)) {
            messErr.add("Mã sinh viên không được để trống");
            fieldErr.add("studentCode");
        } else if (studentCode.length() < 0 || studentCode.length() > 50) {
            messErr.add("Mã sinh viên không được quá 50 ký tự");
            fieldErr.add("studentCode");
        } else {
            if (null != classroomStudent) {
                messErr.add("Mã sinh viên đã tồn tại");
                fieldErr.add("code");
            }
        }

        classroomStudentDTO.setMessageErr(messErr);
        classroomStudentDTO.setFieldErr(fieldErr);
        return classroomStudentDTO;
    }
}
