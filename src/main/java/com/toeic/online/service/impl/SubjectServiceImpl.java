package com.toeic.online.service.impl;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUploadUtil;
import com.toeic.online.commons.Translator;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Classroom;
import com.toeic.online.domain.Subject;
import com.toeic.online.domain.User;
import com.toeic.online.repository.ClassroomRepository;
import com.toeic.online.repository.SubjectRepository;
import com.toeic.online.repository.SubjectRepositoryCustom;
import com.toeic.online.service.SubjectService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepositoryCustom subjectRepositoryCustom;

    private final ClassroomRepository classroomRepository;

    private final SubjectRepository subjectRepository;

    @Value("${import-file.sample-file}")
    private String folderSampleFile;

    private final ExportUtils exportUtils;

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    List<ExcelDynamicDTO> lstError;

    private static final String SHEETNAME = "subject.sheetName";

    public SubjectServiceImpl(
        SubjectRepositoryCustom subjectRepositoryCustom,
        ClassroomRepository classroomRepository,
        SubjectRepository subjectRepository,
        ExportUtils exportUtils,
        FileExportUtil fileExportUtil,
        UserService userService
    ) {
        this.subjectRepositoryCustom = subjectRepositoryCustom;
        this.classroomRepository = classroomRepository;
        this.subjectRepository = subjectRepository;
        this.exportUtils = exportUtils;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
    }

    @Override
    public List<SubjectDTO> exportData(SearchSubjectDTO searchSubjectDTO) {
        return subjectRepositoryCustom.exportData(searchSubjectDTO);
    }

    @Override
    public ByteArrayInputStream getSampleFile() throws IOException {
        String path = folderSampleFile + "DS_Monhoc.xlsx";
        InputStream file = new BufferedInputStream(new FileInputStream(path));

        XSSFWorkbook wb = null;
        try {
            wb = new XSSFWorkbook(new File(path));
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        XSSFSheet sheet = wb.getSheetAt(0);
        file.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wb.write(baos);
        wb.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public Map<String, Object> search(SearchSubjectDTO searchSubjectDTO, Integer page, Integer pageSize) {
        List<SubjectDTO> lstSubject = subjectRepositoryCustom.search(searchSubjectDTO, page, pageSize);
        Integer total = subjectRepositoryCustom.exportData(searchSubjectDTO).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstSubject", lstSubject);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<SubjectDTO> lstSubjectDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstSubjectDTO, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importSubject(MultipartFile fileUploads, Long typeImport) throws Exception {
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

        List<Subject> dataSuccess = new ArrayList<>();
        List<SubjectDTO> dataErrors = new ArrayList<>();
        List<Classroom> lstClassroom = classroomRepository.findAll();
        List<Subject> lstSubjects = subjectRepository.findAll();
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 4) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            SubjectDTO dto = processRecord(record, lstClassroom, lstSubjects, typeImport);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                Subject subject = new Subject(dto);
                dataSuccess.add(subject);
                subject = subjectRepository.save(subject);
                lstSubjects.add(subject);
                dto.setId(subject.getId());
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
    public byte[] exportExcelSubjectErrors(List<SubjectDTO> listDataErrors) throws Exception {
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
        List<SubjectDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr = new String[] { "recordNo", "subject.code", "subject.name", "classroom.code" };
        } else {
            headerArr = new String[] { "recordNo", "subject.code", "subject.name", "classroom.code", "descriptionErrors" };
        }
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            List<Classroom> lstClassroom = classroomRepository.findAll();
            List<String> lstCodeClassroom = lstClassroom.stream().map(Classroom::getCode).collect(Collectors.toList());
            cellConfigCustomList.add(
                new CellConfigDTO("classCode", AppConstants.ALIGN_LEFT, lstCodeClassroom.toArray(new String[0]), 1, 99, 3, 3)
            );

            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    SubjectDTO data = new SubjectDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (SubjectDTO item : lstDataSheet) {
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
        cellConfigList.add(new CellConfigDTO("classCode", AppConstants.ALIGN_LEFT, AppConstants.STRING));

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

    private SubjectDTO processRecord(List<String> record, List<Classroom> lstClassroom, List<Subject> lstSubject, Long typeImport) {
        SubjectDTO subjectDTO = new SubjectDTO();
        List<String> messErr = new ArrayList<>();
        List<String> fieldErr = new ArrayList<>();
        Optional<User> userCreate = userService.getUserWithAuthorities();

        int col = 1;
        if (typeImport == AppConstants.IMPORT_INSERT) {
            subjectDTO.setCreateName(userCreate.get().getLogin());
            subjectDTO.setCreateDate(Instant.now());
        } else {
            subjectDTO.setUpdateName(userCreate.get().getLogin());
            subjectDTO.setUpdateDate(Instant.now());
        }
        String subjectCode = record.get(col++);
        subjectDTO.setCode(subjectCode);
        Subject subject = lstSubject.stream().filter(c -> subjectCode.equalsIgnoreCase(c.getCode())).findAny().orElse(null);
        if (StringUtils.isBlank(subjectCode)) {
            messErr.add("Mã môn học không được để trống");
            fieldErr.add("code");
        } else if (subjectCode.length() < 0 || subjectCode.length() > 50) {
            messErr.add("Mã môn học không được quá 50 ký tự");
            fieldErr.add("code");
        } else if (typeImport == AppConstants.IMPORT_UPDATE) {
            if (null != subject) {
                subject.setId(subject.getId());
                subjectDTO.setCreateDate(subject.getCreateDate());
                subjectDTO.setCreateName(subject.getCreateName());
            }
        } else {
            if (null != subject) {
                messErr.add("Mã môn học đã tồn tại");
                fieldErr.add("code");
            }
        }

        String subjectName = record.get(col++);
        subjectDTO.setName(subjectName);
        if (StringUtils.isBlank(subjectName)) {
            messErr.add("Tên môn học không được để trống");
            fieldErr.add("name");
        } else if (subjectName.length() < 0 || subjectName.length() > 250) {
            messErr.add("Tên môn học không được quá 250 ký tự");
            fieldErr.add("name");
        }

        String classCode = record.get(col++);
        subjectDTO.setClassCode(classCode);
        if (StringUtils.isBlank(classCode)) {
            messErr.add("Mã lớp học không được để trống");
            fieldErr.add("classCode");
        } else if (classCode.length() < 0 || classCode.length() > 50) {
            messErr.add("Mã lớp học không được quá 50 ký tự");
            fieldErr.add("classCode");
        }
        subjectDTO.setStatus(true);
        subjectDTO.setMessageErr(messErr);
        subjectDTO.setFieldErr(fieldErr);
        return subjectDTO;
    }
}
