package com.toeic.online.web.rest;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Classroom;
import com.toeic.online.domain.User;
import com.toeic.online.repository.ClassroomRepository;
import com.toeic.online.service.ClassroomService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.*;
import com.toeic.online.web.rest.errors.BadRequestAlertException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 * REST controller for managing {@link com.toeic.online.domain.Classroom}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ClassroomResource {

    private final Logger log = LoggerFactory.getLogger(ClassroomResource.class);

    private static final String ENTITY_NAME = "classroom";

    private final UserService userService;

    private final ClassroomService classroomService;

    private final ExportUtils exportUtils;

    @Value("${import-file.sample-file}")
    private String subFolder;

    @Value("${import-file.folder}")
    private String folder;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private FileExportUtil fileExportUtil;

    private final ClassroomRepository classroomRepository;

    public ClassroomResource(
        UserService userService,
        ClassroomService classroomService,
        ExportUtils exportUtils,
        ClassroomRepository classroomRepository
    ) {
        this.userService = userService;
        this.classroomService = classroomService;
        this.exportUtils = exportUtils;
        this.classroomRepository = classroomRepository;
    }

    /**
     * {@code POST  /classrooms} : Create a new classroom.
     *
     * @param classroom the classroom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new classroom, or with status {@code 400 (Bad Request)} if the classroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/classrooms")
    public ResponseEntity<Classroom> createClassroom(@RequestBody Classroom classroom) throws URISyntaxException {
        log.debug("REST request to save Classroom : {}", classroom);
        Optional<User> userLogin = userService.getUserWithAuthorities();
        if (classroom.getId() == null) {
            classroom.setCreateName(userLogin.get().getLogin());
            classroom.setCreateDate(new Date().toInstant());
        } else {
            Classroom classroomOld = classroomRepository.findById(classroom.getId()).get();
            classroom.setCreateDate(classroomOld.getCreateDate());
            classroom.setCreateName(classroomOld.getCreateName());
            classroom.setUpdateDate(new Date().toInstant());
            classroom.setUpdateName(userLogin.get().getLogin());
        }
        classroom.setStatus(true);
        Classroom result = classroomRepository.save(classroom);
        return ResponseEntity
            .created(new URI("/api/classrooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /classrooms/:id} : Updates an existing classroom.
     *
     * @param id the id of the classroom to save.
     * @param classroom the classroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroom,
     * or with status {@code 400 (Bad Request)} if the classroom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the classroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> updateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Classroom classroom
    ) throws URISyntaxException {
        log.debug("REST request to update Classroom : {}, {}", id, classroom);
        if (classroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Classroom result = classroomRepository.save(classroom);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classroom.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /classrooms/:id} : Partial updates given fields of an existing classroom, field will ignore if it is null
     *
     * @param id the id of the classroom to save.
     * @param classroom the classroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated classroom,
     * or with status {@code 400 (Bad Request)} if the classroom is not valid,
     * or with status {@code 404 (Not Found)} if the classroom is not found,
     * or with status {@code 500 (Internal Server Error)} if the classroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/classrooms/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Classroom> partialUpdateClassroom(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Classroom classroom
    ) throws URISyntaxException {
        log.debug("REST request to partial update Classroom partially : {}, {}", id, classroom);
        if (classroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, classroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!classroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Classroom> result = classroomRepository
            .findById(classroom.getId())
            .map(
                existingClassroom -> {
                    if (classroom.getCode() != null) {
                        existingClassroom.setCode(classroom.getCode());
                    }
                    if (classroom.getName() != null) {
                        existingClassroom.setName(classroom.getName());
                    }
                    if (classroom.getTeacherCode() != null) {
                        existingClassroom.setTeacherCode(classroom.getTeacherCode());
                    }
                    if (classroom.getStatus() != null) {
                        existingClassroom.setStatus(classroom.getStatus());
                    }
                    if (classroom.getAvatar() != null) {
                        existingClassroom.setAvatar(classroom.getAvatar());
                    }
                    if (classroom.getCreateDate() != null) {
                        existingClassroom.setCreateDate(classroom.getCreateDate());
                    }
                    if (classroom.getCreateName() != null) {
                        existingClassroom.setCreateName(classroom.getCreateName());
                    }
                    if (classroom.getUpdateDate() != null) {
                        existingClassroom.setUpdateDate(classroom.getUpdateDate());
                    }
                    if (classroom.getUpdateName() != null) {
                        existingClassroom.setUpdateName(classroom.getUpdateName());
                    }

                    return existingClassroom;
                }
            )
            .map(classroomRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, classroom.getId().toString())
        );
    }

    /**
     * {@code GET  /classrooms} : get all the classrooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of classrooms in body.
     */
    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms() {
        log.debug("REST request to get all Classrooms");
        return classroomRepository.findAll();
    }

    /**
     * {@code GET  /classrooms/:id} : get the "id" classroom.
     *
     * @param id the id of the classroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the classroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> getClassroom(@PathVariable Long id) {
        log.debug("REST request to get Classroom : {}", id);
        Optional<Classroom> classroom = classroomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(classroom);
    }

    /**
     * {@code DELETE  /classrooms/:id} : delete the "id" classroom.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/classrooms/delete")
    public ResponseEntity<?> deleteClassroom(@RequestBody Classroom classroom) {
        log.debug("REST request to delete Classroom : {}", classroom);
        Classroom classroomOld = classroomRepository.findById(classroom.getId()).get();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        classroomOld.setStatus(false);
        classroomOld.setUpdateDate(Instant.now());
        classroomOld.setUpdateName(userCreate.get().getLogin());
        classroomRepository.save(classroomOld);
        return ResponseEntity.ok().body(classroomOld);
    }

    @PostMapping("classrooms/export")
    public ResponseEntity<?> export(@RequestBody ClassroomSearchDTO classroomSearchDTO) throws Exception {
        List<ClassroomDTO> listData = classroomService.exportData(classroomSearchDTO);
        List<ExcelColumn> lstColumn = buildColumnExport();
        String title = "Danh sách lớp học";
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
        lstColumn.add(new ExcelColumn("code", "Mã lớp học", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("name", "Tên lớp học", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("teacherName", "Tên giáo viên", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("statusStr", "Trạng thái", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createDate", "Ngày tạo", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createName", "Người tạo", ExcelColumn.ALIGN_MENT.LEFT));
        return lstColumn;
    }

    @GetMapping("/classrooms/sample-file")
    public ResponseEntity<?> getSampleFile() throws IOException {
        log.debug("REST request to download file sample");
        try {
            ByteArrayInputStream bais = classroomService.getSampleFile();
            InputStreamResource resource = new InputStreamResource(bais);
            return ResponseEntity
                .ok()
                .contentLength(bais.available())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/classrooms/search")
    public ResponseEntity<?> search(
        @RequestBody ClassroomSearchDTO classroomSearchDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = classroomService.search(classroomSearchDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    // Import data by excel
    @PostMapping("/classrooms/importData")
    public ServiceResult<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("typeImport") Long typeImport)
        throws Exception {
        return classroomService.importClassroom(file, typeImport);
    }

    @PostMapping("/classrooms/exportTemplate")
    public ResponseEntity<?> exportTemplate() throws Exception {
        log.info("REST request to export template Teacher");
        byte[] fileData = classroomService.exportFileTemplate();
        String fileName = "DS_Lophoc" + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/classrooms/exportDataErrors")
    public ResponseEntity<?> exportDataErrors(@RequestBody List<ClassroomDTO> lstError) throws Exception {
        byte[] fileData = classroomService.exportExcelClassroomErrors(lstError);
        String fileName = "DS_Lophoc_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
