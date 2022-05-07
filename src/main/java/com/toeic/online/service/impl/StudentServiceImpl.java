package com.toeic.online.service.impl;

import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUploadUtil;
import com.toeic.online.commons.Translator;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Authority;
import com.toeic.online.domain.Student;
import com.toeic.online.domain.Teacher;
import com.toeic.online.domain.User;
import com.toeic.online.repository.StudentRepository;
import com.toeic.online.repository.UserRepository;
import com.toeic.online.repository.impl.StudentRepositoryCustomImpl;
import com.toeic.online.service.StudentService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepositoryCustomImpl studentRepositoryCustom;

    private final StudentRepository studentRepository;

    private final UserService userService;

    private static final String SHEETNAME = "student.sheetName";

    private final FileExportUtil fileExportUtil;

    private final UserRepository userRepository;

    List<ExcelDynamicDTO> lstError;

    public StudentServiceImpl(
        StudentRepositoryCustomImpl studentRepositoryCustom,
        StudentRepository studentRepository,
        UserService userService,
        FileExportUtil fileExportUtil,
        UserRepository userRepository
    ) {
        this.studentRepositoryCustom = studentRepositoryCustom;
        this.studentRepository = studentRepository;
        this.userService = userService;
        this.fileExportUtil = fileExportUtil;
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, Object> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize) {
        List<StudentDTO> lstStudent = studentRepositoryCustom.search(searchTeacherDTO, page, pageSize);
        Integer total = studentRepositoryCustom.exportData(searchTeacherDTO).size();
        Map<String, Object> res = new HashMap<>();
        res.put("lstStudent", lstStudent);
        res.put("totalRecord", total);
        return res;
    }

    @Override
    public List<StudentDTO> exportData(SearchTeacherDTO searchTeacherDTO) {
        return studentRepositoryCustom.exportData(searchTeacherDTO);
    }

    @Override
    public ByteArrayInputStream getSampleFile() throws IOException {
        return null;
    }

    @Override
    public byte[] exportFileTemplate() throws Exception {
        List<SheetConfigDTO> lstSheetConfigDTO = new ArrayList<>();
        List<StudentDTO> lstStudent = new ArrayList<>();
        lstSheetConfigDTO = getDataForExcel(SHEETNAME, lstStudent, lstSheetConfigDTO, AppConstants.EXPORT_TEMPLATE);
        try {
            return fileExportUtil.exportXLSX(true, lstSheetConfigDTO, null);
        } catch (IOException ioE) {
            throw new Exception("Error When Export excel file in Resale sales double deposit total: " + ioE.getMessage(), ioE);
        }
    }

    @Override
    public ServiceResult<?> importStudent(MultipartFile fileUploads, Long typeImport) throws Exception {
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

        List<Student> dataSuccess = new ArrayList<>();
        List<StudentDTO> dataErrors = new ArrayList<>();
        List<User> users = new ArrayList<>();
        List<Student> lstStudent = studentRepository.findAll();
        int countSuccess = 0;
        int countError = 0;
        int total = 0;
        for (List<String> record : records) {
            if (record.size() != 5) {
                return new ServiceResult<>(null, HttpStatus.BAD_REQUEST, Translator.toLocale("msg.file.errorTemplate"));
            }

            StudentDTO dto = processRecord(record, lstStudent, typeImport);
            if (dto.getMessageErr().size() > 0) {
                countError++;
                dataErrors.add(dto);
            } else {
                Student student = new Student(dto);
                dataSuccess.add(student);
                student = studentRepository.save(student);
                lstStudent.add(student);
                dto.setId(student.getId());
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
    public byte[] exportExcelStudentErrors(List<StudentDTO> listDataErrors) throws Exception {
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
        List<StudentDTO> lstDataSheet,
        List<SheetConfigDTO> lstSheetConfigDTO,
        Long exportType
    ) {
        SheetConfigDTO sheetConfig = new SheetConfigDTO();
        String[] headerArr = null;
        if (exportType == 0L) {
            headerArr = new String[] { "recordNo", "student.code", "student.fullname", "student.phone", "student.email" };
        } else {
            headerArr =
                new String[] { "recordNo", "student.code", "student.fullname", "student.phone", "student.email", "descriptionErrors" };
        }
        sheetConfig.setSheetName(sheetName);
        sheetConfig.setHeaders(headerArr);
        int recordNo = 1;
        List<CellConfigDTO> cellConfigCustomList = new ArrayList<>();
        if (exportType != AppConstants.EXPORT_DATA) {
            if (exportType != AppConstants.EXPORT_ERRORS) {
                for (int i = 1; i < 4; i++) {
                    StudentDTO data = new StudentDTO();
                    data.setRecordNo(i);
                    lstDataSheet.add(data);
                }
            }
            if (exportType == AppConstants.EXPORT_ERRORS) {
                for (StudentDTO item : lstDataSheet) {
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

    private StudentDTO processRecord(List<String> record, List<Student> lstStudent, Long typeImport) {
        StudentDTO studentDTO = new StudentDTO();
        List<String> messErr = new ArrayList<>();
        List<String> fieldErr = new ArrayList<>();
        Optional<User> userCreate = userService.getUserWithAuthorities();

        int col = 1;
        if (typeImport == AppConstants.IMPORT_INSERT) {
            studentDTO.setCreateName(userCreate.get().getLogin());
            studentDTO.setCreateDate(Instant.now());
        } else {
            studentDTO.setUpdateName(userCreate.get().getLogin());
            studentDTO.setUpdateDate(Instant.now());
        }
        String studentCode = record.get(col++);
        studentDTO.setCode(studentCode);
        Student student = lstStudent.stream().filter(t -> studentCode.equalsIgnoreCase(t.getCode())).findAny().orElse(null);
        if (StringUtils.isBlank(studentCode)) {
            messErr.add(Translator.toLocale("student.error.code.blank"));
            fieldErr.add("code");
        } else if (studentCode.length() < 0 || studentCode.length() > 50) {
            messErr.add(Translator.toLocale("student.error.code.lenght"));
            fieldErr.add("code");
        } else if (typeImport == AppConstants.IMPORT_UPDATE) {
            if (null != student) {
                studentDTO.setId(student.getId());
                studentDTO.setCreateDate(student.getCreateDate());
                studentDTO.setCreateName(student.getCreateName());
            }
        } else {
            if (null != student) {
                messErr.add(Translator.toLocale("student.error.code.exit"));
                fieldErr.add("code");
            }
        }

        String fullName = record.get(col++);
        studentDTO.setFullName(fullName);
        if (StringUtils.isBlank(fullName)) {
            messErr.add(Translator.toLocale("student.error.fullname.blank"));
            fieldErr.add("fullName");
        } else if (fullName.length() < 0 || fullName.length() > 250) {
            messErr.add(Translator.toLocale("student.error.fullname.lenght"));
            fieldErr.add("fullName");
        }

        String phone = record.get(col++);
        studentDTO.setPhone(phone);
        if (phone.matches(AppConstants.REGEX_NUMBER)) {
            messErr.add(Translator.toLocale("student.error.phoneErrFormat"));
            fieldErr.add("phone");
        }

        String email = record.get(col++);
        studentDTO.setEmail(email);
        if (email.trim().length() > 5) studentDTO.setEmail(email.trim()); else studentDTO.setEmail(null);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(email) && email.trim().length() > 250) {
            messErr.add(Translator.toLocale("student.error.email.lenght"));
            fieldErr.add("email");
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(email) && !email.matches(AppConstants.REGEX_EMAIL_2)) {
            messErr.add(Translator.toLocale("student.error.email.format"));
            fieldErr.add("email");
        }
        studentDTO.setStatus(true);
        studentDTO.setMessageErr(messErr);
        studentDTO.setFieldErr(fieldErr);
        return studentDTO;
    }

    private User convertToUser(StudentDTO dto) {
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
        authority.setName("ROLE_SV");
        authorities.add(authority);
        user.setAuthorities(authorities);
        return user;
    }
}
