package com.toeic.online.web.rest;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.commons.FileUtils;
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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

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

    @GetMapping("/classrooms")
    public List<Classroom> getAllClassrooms() {
        log.debug("REST request to get all Classrooms");
        return classroomRepository.findAll();
    }

    @GetMapping("/classrooms/{id}")
    public ResponseEntity<Classroom> getClassroom(@PathVariable Long id) {
        log.debug("REST request to get Classroom : {}", id);
        Optional<Classroom> classroom = classroomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(classroom);
    }

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
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.YYYYMMDDHHSS);
        String fileName = "DS_Lophoc_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
