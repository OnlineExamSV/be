package com.toeic.online.web.rest;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Authority;
import com.toeic.online.domain.Teacher;
import com.toeic.online.domain.User;
import com.toeic.online.repository.TeacherRepository;
import com.toeic.online.service.TeacherService;
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
 * REST controller for managing {@link com.toeic.online.domain.Teacher}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TeacherResource {

    private final Logger log = LoggerFactory.getLogger(TeacherResource.class);

    private static final String ENTITY_NAME = "teacher";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeacherRepository teacherRepository;

    private final UserService userService;

    private final TeacherService teacherService;

    private final ExportUtils exportUtils;

    @Autowired
    private FileExportUtil fileExportUtil;

    public TeacherResource(
        TeacherRepository teacherRepository,
        UserService userService,
        TeacherService teacherService,
        ExportUtils exportUtils
    ) {
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.teacherService = teacherService;
        this.exportUtils = exportUtils;
    }

    /**
     * {@code POST  /teachers} : Create a new teacher.
     *
     * @param teacher the teacher to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teacher, or with status {@code 400 (Bad Request)} if the teacher has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) throws URISyntaxException {
        log.debug("REST request to save Teacher : {}", teacher);
        //        if (teacher.getId() != null) {
        //            throw new BadRequestAlertException("A new teacher cannot already have an ID", ENTITY_NAME, "idexists");
        //        }
        Optional<User> userCreate = userService.getUserWithAuthorities();
        Long idTeacher = teacher.getId();
        if (teacher.getId() == null) {
            teacher.setCreateName(userCreate.get().getLogin());
            teacher.setCreateDate(Instant.now());
        } else {
            Teacher teacherOld = teacherRepository.findById(teacher.getId()).get();
            teacher.setCreateName(teacherOld.getCreateName());
            teacher.setCreateDate(teacherOld.getCreateDate());
            teacher.setUpdateDate(Instant.now());
            teacher.setUpdateName(userCreate.get().getLogin());
        }
        teacher.setCode(teacher.getCode().toLowerCase());
        teacher.setStatus(true);
        Teacher result = teacherRepository.save(teacher);
        // Lưu thông gv vào bảng jhi_use
        if (idTeacher == null) {
            User user = new User();
            user.setLogin(teacher.getCode());
            user.setFullName(teacher.getFullName());
            user.setPhoneNumber(teacher.getPhone());
            user.setImageUrl(teacher.getAvatar());
            user.setActivated(true);
            user.setEmail(teacher.getEmail());
            user.setCreatedDate(Instant.now());
            user.setCreatedBy(teacher.getCreateName());
            user.setLastModifiedBy(teacher.getCreateName());
            user.setLastModifiedDate(Instant.now());
            Set<Authority> authorities = new HashSet<>();
            Authority authority = new Authority("ROLE_GV");
            authorities.add(authority);
            user.setAuthorities(authorities);
            user = userService.save(user);
        } else {
            User userUpdate = userService.findByLogin(teacher.getCode()).get();
            userUpdate.setLogin(teacher.getCode());
            userUpdate.setFullName(teacher.getFullName());
            userUpdate.setPhoneNumber(teacher.getPhone());
            userUpdate.setImageUrl(teacher.getAvatar());
            userUpdate.setEmail(teacher.getEmail());
            userUpdate = userService.update(userUpdate);
        }
        return ResponseEntity
            .created(new URI("/api/teachers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /teachers/:id} : Updates an existing teacher.
     *
     * @param id the id of the teacher to save.
     * @param teacher the teacher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacher,
     * or with status {@code 400 (Bad Request)} if the teacher is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teacher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable(value = "id", required = false) final Long id, @RequestBody Teacher teacher)
        throws URISyntaxException {
        log.debug("REST request to update Teacher : {}, {}", id, teacher);
        if (teacher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Teacher result = teacherRepository.save(teacher);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacher.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /teachers/:id} : Partial updates given fields of an existing teacher, field will ignore if it is null
     *
     * @param id the id of the teacher to save.
     * @param teacher the teacher to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teacher,
     * or with status {@code 400 (Bad Request)} if the teacher is not valid,
     * or with status {@code 404 (Not Found)} if the teacher is not found,
     * or with status {@code 500 (Internal Server Error)} if the teacher couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/teachers/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Teacher> partialUpdateTeacher(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Teacher teacher
    ) throws URISyntaxException {
        log.debug("REST request to partial update Teacher partially : {}, {}", id, teacher);
        if (teacher.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teacher.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!teacherRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Teacher> result = teacherRepository
            .findById(teacher.getId())
            .map(
                existingTeacher -> {
                    if (teacher.getCode() != null) {
                        existingTeacher.setCode(teacher.getCode());
                    }
                    if (teacher.getFullName() != null) {
                        existingTeacher.setFullName(teacher.getFullName());
                    }
                    if (teacher.getEmail() != null) {
                        existingTeacher.setEmail(teacher.getEmail());
                    }
                    if (teacher.getPhone() != null) {
                        existingTeacher.setPhone(teacher.getPhone());
                    }
                    if (teacher.getStatus() != null) {
                        existingTeacher.setStatus(teacher.getStatus());
                    }
                    if (teacher.getAvatar() != null) {
                        existingTeacher.setAvatar(teacher.getAvatar());
                    }
                    if (teacher.getCreateDate() != null) {
                        existingTeacher.setCreateDate(teacher.getCreateDate());
                    }
                    if (teacher.getCreateName() != null) {
                        existingTeacher.setCreateName(teacher.getCreateName());
                    }
                    if (teacher.getUpdateDate() != null) {
                        existingTeacher.setUpdateDate(teacher.getUpdateDate());
                    }
                    if (teacher.getUpdateName() != null) {
                        existingTeacher.setUpdateName(teacher.getUpdateName());
                    }

                    return existingTeacher;
                }
            )
            .map(teacherRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, teacher.getId().toString())
        );
    }

    /**
     * {@code GET  /teachers} : get all the teachers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teachers in body.
     */
    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        log.debug("REST request to get all Teachers");
        return teacherRepository.findAll();
    }

    /**
     * {@code GET  /teachers/:id} : get the "id" teacher.
     *
     * @param id the id of the teacher to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teacher, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable Long id) {
        log.debug("REST request to get Teacher : {}", id);
        Optional<Teacher> teacher = teacherRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(teacher);
    }

    /**
     * {@code DELETE  /teachers/:id} : delete the "id" teacher.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/teachers/delete")
    public ResponseEntity<?> deleteTeacher(@RequestBody Teacher teacher) {
        log.debug("REST request to delete Teacher : {}", teacher);
        Teacher teacherUpdate = teacherRepository.findById(teacher.getId()).get();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        teacherUpdate.setUpdateName(userCreate.get().getLogin());
        teacherUpdate.setUpdateDate(Instant.now());
        teacherUpdate.setStatus(false);
        teacherUpdate = teacherRepository.save(teacherUpdate);
        // Update lại trạng thái user
        User userUpdate = userService.findByLogin(teacher.getCode()).get();
        if (userUpdate != null) {
            userUpdate.setActivated(false);
            userUpdate = userService.update(userUpdate);
        }
        return ResponseEntity.ok().body(teacherUpdate);
    }

    @PostMapping("/teachers/search")
    public ResponseEntity<?> search(
        @RequestBody SearchTeacherDTO searchTeacherDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = teacherService.search(searchTeacherDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/teachers/export")
    public ResponseEntity<?> export(@RequestBody SearchTeacherDTO searchTeacherDTO) throws Exception {
        List<TeacherDTO> listData = teacherService.exportData(searchTeacherDTO);
        List<ExcelColumn> lstColumn = buildColumnExport();
        String title = "Danh sách giảng viên";
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
        lstColumn.add(new ExcelColumn("code", "Mã giảng viên", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("fullName", "Tên giảng viên", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("email", "Email", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("phone", "Số điện thoại", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createDate", "Ngày tạo", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createName", "Người tạo", ExcelColumn.ALIGN_MENT.LEFT));
        return lstColumn;
    }

    @PostMapping("/teachers/importData")
    public ServiceResult<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("typeImport") Long typeImport)
        throws Exception {
        return teacherService.importTeacher(file, typeImport);
    }

    @PostMapping("/teachers/exportTemplate")
    public ResponseEntity<?> exportTemplate() throws Exception {
        log.info("REST request to export template Teacher");
        byte[] fileData = teacherService.exportFileTemplate();
        String fileName = "DS_GV" + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/teachers/exportDataErrors")
    public ResponseEntity<?> exportDataErrors(@RequestBody List<TeacherDTO> lstError) throws Exception {
        byte[] fileData = teacherService.exportExcelTeacherErrors(lstError);
        String fileName = "DS_GV_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
