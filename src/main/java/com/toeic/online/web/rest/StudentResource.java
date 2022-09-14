package com.toeic.online.web.rest;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Authority;
import com.toeic.online.domain.Student;
import com.toeic.online.domain.Teacher;
import com.toeic.online.domain.User;
import com.toeic.online.repository.StudentRepository;
import com.toeic.online.service.StudentService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import com.toeic.online.web.rest.errors.BadRequestAlertException;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.toeic.online.domain.Student}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class StudentResource {

    private final Logger log = LoggerFactory.getLogger(StudentResource.class);

    private static final String ENTITY_NAME = "student";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StudentRepository studentRepository;

    private final StudentService studentService;

    private final ExportUtils exportUtils;

    private final UserService userService;

    @Autowired
    private FileExportUtil fileExportUtil;

    public StudentResource(
        StudentRepository studentRepository,
        StudentService studentService,
        ExportUtils exportUtils,
        UserService userService
    ) {
        this.studentRepository = studentRepository;
        this.studentService = studentService;
        this.exportUtils = exportUtils;
        this.userService = userService;
    }

    /**
     * {@code POST  /students} : Create a new student.
     *
     * @param student the student to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new student, or with status {@code 400 (Bad Request)} if the student has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) throws URISyntaxException {
        Long idStudent = student.getId();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        if (student.getId() == null) {
            student.createDate(Instant.now());
            student.createName(userCreate.get().getLogin());
        } else {
            Student studentOld = studentRepository.findById(student.getId()).get();
            student.createDate(studentOld.getCreateDate());
            student.createName(studentOld.getCreateName());
            student.updateDate(Instant.now());
            student.updateName(userCreate.get().getLogin());
        }
        student.setCode(student.getCode().toLowerCase());
        student.setStatus(true);
        Student result = studentRepository.save(student);
        if (idStudent == null) {
            User user = new User();
            user.setLogin(student.getCode());
            user.setFullName(student.getFullName());
            user.setPhoneNumber(student.getPhone());
            user.setImageUrl(student.getAvatar());
            user.setActivated(true);
            user.setEmail(student.getEmail());
            user.setCreatedDate(Instant.now());
            user.setCreatedBy(student.getCreateName());
            user.setLastModifiedBy(student.getCreateName());
            user.setLastModifiedDate(Instant.now());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = new Authority("ROLE_SV");
            authorities.add(authority);
            user.setAuthorities(authorities);
            user = userService.save(user);
        } else {
            User userUpdate = userService.findByLogin(student.getCode()).get();
            userUpdate.setLogin(student.getCode());
            userUpdate.setFullName(student.getFullName());
            userUpdate.setPhoneNumber(student.getPhone());
            userUpdate.setImageUrl(student.getAvatar());
            userUpdate.setEmail(student.getEmail());
            userUpdate = userService.update(userUpdate);
        }
        return ResponseEntity
            .created(new URI("/api/students/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /students/:id} : Updates an existing student.
     *
     * @param id the id of the student to save.
     * @param student the student to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated student,
     * or with status {@code 400 (Bad Request)} if the student is not valid,
     * or with status {@code 500 (Internal Server Error)} if the student couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable(value = "id", required = false) final Long id, @RequestBody Student student)
        throws URISyntaxException {
        log.debug("REST request to update Student : {}, {}", id, student);
        if (student.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, student.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Student result = studentRepository.save(student);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, student.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /students/:id} : Partial updates given fields of an existing student, field will ignore if it is null
     *
     * @param id the id of the student to save.
     * @param student the student to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated student,
     * or with status {@code 400 (Bad Request)} if the student is not valid,
     * or with status {@code 404 (Not Found)} if the student is not found,
     * or with status {@code 500 (Internal Server Error)} if the student couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/students/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Student> partialUpdateStudent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Student student
    ) throws URISyntaxException {
        log.debug("REST request to partial update Student partially : {}, {}", id, student);
        if (student.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, student.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!studentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Student> result = studentRepository
            .findById(student.getId())
            .map(
                existingStudent -> {
                    if (student.getCode() != null) {
                        existingStudent.setCode(student.getCode());
                    }
                    if (student.getFullName() != null) {
                        existingStudent.setFullName(student.getFullName());
                    }
                    if (student.getEmail() != null) {
                        existingStudent.setEmail(student.getEmail());
                    }
                    if (student.getPhone() != null) {
                        existingStudent.setPhone(student.getPhone());
                    }
                    if (student.getStatus() != null) {
                        existingStudent.setStatus(student.getStatus());
                    }
                    if (student.getAvatar() != null) {
                        existingStudent.setAvatar(student.getAvatar());
                    }
                    if (student.getCreateDate() != null) {
                        existingStudent.setCreateDate(student.getCreateDate());
                    }
                    if (student.getCreateName() != null) {
                        existingStudent.setCreateName(student.getCreateName());
                    }
                    if (student.getUpdateDate() != null) {
                        existingStudent.setUpdateDate(student.getUpdateDate());
                    }
                    if (student.getUpdateName() != null) {
                        existingStudent.setUpdateName(student.getUpdateName());
                    }

                    return existingStudent;
                }
            )
            .map(studentRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, student.getId().toString())
        );
    }

    /**
     * {@code GET  /students} : get all the students.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of students in body.
     */
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        log.debug("REST request to get all Students");
        return studentRepository.findAll();
    }

    /**
     * {@code GET  /students/:id} : get the "id" student.
     *
     * @param id the id of the student to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the student, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Long id) {
        log.debug("REST request to get Student : {}", id);
        Optional<Student> student = studentRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(student);
    }

    /**
     * {@code DELETE  /students/:id} : delete the "id" student.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/students/delete")
    public ResponseEntity<?> deleteStudents(@RequestBody Student student) {
        log.debug("REST request to delete Student : {}", student);
        Student studentUpdate = studentRepository.findById(student.getId()).get();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        studentUpdate.setUpdateName(userCreate.get().getLogin());
        studentUpdate.setUpdateDate(Instant.now());
        studentUpdate.setStatus(false);
        studentUpdate = studentRepository.save(studentUpdate);
        User userUpdate = userService.findByLogin(student.getCode()).get();
        if (userUpdate != null) {
            userUpdate.setActivated(false);
            userUpdate = userService.update(userUpdate);
        }
        return ResponseEntity.ok().body(studentUpdate);
    }

    @PostMapping("/students/search")
    public ResponseEntity<?> search(
        @RequestBody SearchTeacherDTO searchTeacherDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = studentService.search(searchTeacherDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/students/export")
    public ResponseEntity<?> export(@RequestBody SearchTeacherDTO searchTeacherDTO) throws Exception {
        List<StudentDTO> listData = studentService.exportData(searchTeacherDTO);
        List<ExcelColumn> lstColumn = buildColumnExport();
        String title = "Danh sách sinh viên";
        ExcelTitle excelTitle = new ExcelTitle(title, "", "");
        ByteArrayInputStream byteArrayInputStream = exportUtils.onExport(lstColumn, listData, 3, 0, excelTitle, true);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
        return ResponseEntity
            .ok()
            .contentLength(byteArrayInputStream.available())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }

    private List<ExcelColumn> buildColumnExport() {
        List<ExcelColumn> lstColumn = new ArrayList<>();
        lstColumn.add(new ExcelColumn("code", "Mã sinh viên", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("fullName", "Tên sinh viên", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("email", "Email", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("phone", "Số điện thoại", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createDate", "Ngày tạo", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createName", "Người tạo", ExcelColumn.ALIGN_MENT.LEFT));
        return lstColumn;
    }

    @PostMapping("/students/importData")
    public ServiceResult<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("typeImport") Long typeImport)
        throws Exception {
        return studentService.importStudent(file, typeImport);
    }

    @PostMapping("/students/exportTemplate")
    public ResponseEntity<?> exportTemplate() throws Exception {
        log.info("REST request to export template Teacher");
        byte[] fileData = studentService.exportFileTemplate();
        String fileName = "DS_SV" + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/students/exportDataErrors")
    public ResponseEntity<?> exportDataErrors(@RequestBody List<StudentDTO> lstError) throws Exception {
        byte[] fileData = studentService.exportExcelStudentErrors(lstError);
        String fileName = "DS_SV_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
