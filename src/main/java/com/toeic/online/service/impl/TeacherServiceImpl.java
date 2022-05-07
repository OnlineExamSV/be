package com.toeic.online.service.impl;

import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUploadUtil;
import com.toeic.online.commons.Translator;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.*;
import com.toeic.online.repository.TeacherRepository;
import com.toeic.online.repository.TeacherRepositoryCustom;
import com.toeic.online.repository.UserRepository;
import com.toeic.online.service.TeacherService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.*;
import java.time.Instant;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepositoryCustom teacherRepositoryCustom;

    private final TeacherRepository teacherRepository;

    private final UserRepository userRepository;

    @Value("${import-file.sample-file}")
    private String folderSampleFile;

    private final FileExportUtil fileExportUtil;

    private final UserService userService;

    List<ExcelDynamicDTO> lstError;

    private static final String SHEETNAME = "teacher.sheetName";

    public TeacherServiceImpl(
        TeacherRepositoryCustom teacherRepositoryCustom,
        TeacherRepository teacherRepository,
        UserRepository userRepository,
        FileExportUtil fileExportUtil,
        UserService userService
    ) {
        this.teacherRepositoryCustom = teacherRepositoryCustom;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.fileExportUtil = fileExportUtil;
        this.userService = userService;
    }

    @Override
    public Map<String, Object> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize) {
        List<TeacherDTO> lstTeacher = teacherRepositoryCustom.search(searchTeacherDTO, page, pageSize);
        Integer total = teacherRepositoryCustom.exportData(searchTeacherDTO).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstTeacher", lstTeacher);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public List<TeacherDTO> exportData(SearchTeacherDTO searchTeacherDTO) {
        return teacherRepositoryCustom.exportData(searchTeacherDTO);
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<TeacherDTO> lstTeacherDTO = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstTeacherDTO, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importTeacher(MultipartFile fileUploads, Long typeImport) throws Exception {
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

        List<Teacher> dataSuccess = new ArrayList<>();
        List<TeacherDTO> dataErrors = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Teacher> lstTeacher = teacherRepository.findAll();
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 5) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            TeacherDTO dto = processRecord(record, lstTeacher, typeImport);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                Teacher teacher = new Teacher(dto);
                dataSuccess.add(teacher);
                teacher = teacherRepository.save(teacher);
                lstTeacher.add(teacher);
                dto.setId(teacher.getId());
                users.add(convertToUser(dto));
                countSuccess++;
            }
            total++;
        }

        // Update user
        if (typeImport == AppConstants.IMPORT_INSERT) {
            if (users.size() > 0) userRepository.saveAll(users);
        } else {
            // Update user
            for (User u : users) {
                Optional<User> userOptional = userRepository.findOneByLogin(u.getLogin());
                User user = userOptional.get();
                user.setFullName(u.getFullName());
                user.setEmail(u.getEmail());
                user.setPhoneNumber(u.getPhoneNumber());
                user.setLastModifiedBy(u.getCreatedBy());
                user.setLastModifiedDate(Instant.now());
                user.setAuthorities(u.getAuthorities());
                userRepository.save(user);
            }
        }

        ResponseImportDTO responseImportDTO = new ResponseImportDTO(countError, countSuccess, total);
        responseImportDTO.setListErrors(dataErrors);
        responseImportDTO.setListSuccess(dataSuccess);
        return new ServiceResult<>(responseImportDTO, HttpStatus.OK, Translator.toLocale("msg.import.success"));
    }

    @Override
    public byte[] exportExcelTeacherErrors(List<TeacherDTO> listDataErrors) throws Exception {
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
        List<TeacherDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr = new String[] { "recordNo", "teacher.code", "teacher.fullname", "teacher.phone", "teacher.email" };
        } else {
            headerArr =
                new String[] { "recordNo", "teacher.code", "teacher.fullname", "teacher.phone", "teacher.email", "descriptionErrors" };
        }
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    TeacherDTO data = new TeacherDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (TeacherDTO item : lstDataSheet) {
                    item.setRecordNo(recordNo++);
                    item.setMessageStr(String.join(AppConstants.NEXT_LINE, item.getMessageErr()));
                }
            }
        }

        sheetConfig.setList(lstDataSheet);
        List<CellConfigDTO> cellConfigList = new ArrayList<>();

        cellConfigList.add(new CellConfigDTO("recordNo", AppConstants.ALIGN_RIGHT, AppConstants.NO));
        cellConfigList.add(new CellConfigDTO("code", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("fullName", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("phone", AppConstants.ALIGN_LEFT, AppConstants.STRING));
        cellConfigList.add(new CellConfigDTO("email", AppConstants.ALIGN_LEFT, AppConstants.STRING));

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

    private TeacherDTO processRecord(List<String> record, List<Teacher> lstTeacher, Long typeImport) {
        TeacherDTO teacherDTO = new TeacherDTO();
        List<String> messErr = new ArrayList<>();
        List<String> fieldErr = new ArrayList<>();
        Optional<User> userCreate = userService.getUserWithAuthorities();

        int col = 1;
        if (typeImport == AppConstants.IMPORT_INSERT) {
            teacherDTO.setCreateName(userCreate.get().getLogin());
            teacherDTO.setCreateDate(Instant.now());
        } else {
            teacherDTO.setUpdateName(userCreate.get().getLogin());
            teacherDTO.setUpdateDate(Instant.now());
        }
        String teacherCode = record.get(col++);
        teacherDTO.setCode(teacherCode);
        Teacher teacher = lstTeacher.stream().filter(t -> teacherCode.equalsIgnoreCase(t.getCode())).findAny().orElse(null);
        if (StringUtils.isBlank(teacherCode)) {
            messErr.add(Translator.toLocale("teacher.error.code.blank"));
            fieldErr.add("code");
        } else if (teacherCode.length() < 0 || teacherCode.length() > 50) {
            messErr.add(Translator.toLocale("teacher.error.code.lenght"));
            fieldErr.add("code");
        } else if (typeImport == AppConstants.IMPORT_UPDATE) {
            if (null != teacher) {
                teacherDTO.setId(teacher.getId());
                teacherDTO.setCreateDate(teacher.getCreateDate());
                teacherDTO.setCreateName(teacher.getCreateName());
            }
        } else {
            if (null != teacher) {
                messErr.add(Translator.toLocale("teacher.error.code.exit"));
                fieldErr.add("code");
            }
        }

        String fullName = record.get(col++);
        teacherDTO.setFullName(fullName);
        if (StringUtils.isBlank(fullName)) {
            messErr.add(Translator.toLocale("teacher.error.fullname.blank"));
            fieldErr.add("fullName");
        } else if (fullName.length() < 0 || fullName.length() > 250) {
            messErr.add(Translator.toLocale("teacher.error.fullname.lenght"));
            fieldErr.add("fullName");
        }

        String phone = record.get(col++);
        teacherDTO.setPhone(phone);
        if (phone.matches(AppConstants.REGEX_NUMBER)) {
            messErr.add(Translator.toLocale("teacher.error.phoneErrFormat"));
            fieldErr.add("phone");
        }

        String email = record.get(col++);
        teacherDTO.setEmail(email);
        if (email.trim().length() > 5) teacherDTO.setEmail(email.trim()); else teacherDTO.setEmail(null);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(email) && email.trim().length() > 250) {
            messErr.add(Translator.toLocale("teacher.error.email.lenght"));
            fieldErr.add("email");
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(email) && !email.matches(AppConstants.REGEX_EMAIL_2)) {
            messErr.add(Translator.toLocale("teacher.error.email.format"));
            fieldErr.add("email");
        }
        teacherDTO.setStatus(true);
        teacherDTO.setMessageErr(messErr);
        teacherDTO.setFieldErr(fieldErr);
        return teacherDTO;
    }

    private User convertToUser(TeacherDTO dto) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setLogin(dto.getCode());
        user.setEmail(dto.getEmail());
        user.setActivated(true);
        user.setPassword(bCryptPasswordEncoder.encode(AppConstants.PASS_DEFAULT));
        user.setPhoneNumber(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setLastName(dto.getFullName());
        user.setCreatedBy(dto.getCreateName());
        user.setCreatedDate(new Date().toInstant());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName("ROLE_GV");
        authorities.add(authority);
        user.setAuthorities(authorities);
        return user;
    }
}
