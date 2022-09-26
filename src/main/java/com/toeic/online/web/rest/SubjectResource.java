package com.toeic.online.web.rest;

import com.toeic.online.commons.ExportUtils;
import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Subject;
import com.toeic.online.domain.User;
import com.toeic.online.repository.SubjectRepository;
import com.toeic.online.service.SubjectService;
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
public class SubjectResource {

    private final Logger log = LoggerFactory.getLogger(SubjectResource.class);

    private static final String ENTITY_NAME = "subject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SubjectRepository subjectRepository;

    private final UserService userService;

    private final SubjectService subjectService;

    private final ExportUtils exportUtils;

    @Autowired
    private FileExportUtil fileExportUtil;

    public SubjectResource(
        SubjectRepository subjectRepository,
        UserService userService,
        SubjectService subjectService,
        ExportUtils exportUtils
    ) {
        this.subjectRepository = subjectRepository;
        this.userService = userService;
        this.subjectService = subjectService;
        this.exportUtils = exportUtils;
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) throws URISyntaxException {
        Optional<User> userLogin = userService.getUserWithAuthorities();
        if (subject.getId() == null) {
            subject.setCreateName(userLogin.get().getLogin());
            subject.setCreateDate(new Date().toInstant());
        } else {
            Subject subjectOld = subjectRepository.findById(subject.getId()).get();
            subject.setCreateName(subjectOld.getCreateName());
            subject.setCreateDate(subjectOld.getCreateDate());
            subject.setUpdateName(userLogin.get().getLogin());
            subject.setUpdateDate(new Date().toInstant());
        }
        subject.setStatus(true);
        Subject result = subjectRepository.save(subject);
        return ResponseEntity
            .created(new URI("/api/subjects/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @GetMapping("/subjects")
    public List<Subject> getAllSubjects() {
        log.debug("REST request to get all Subjects");
        return subjectRepository.findAll();
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subject> getSubject(@PathVariable Long id) {
        log.debug("REST request to get Subject : {}", id);
        Optional<Subject> subject = subjectRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(subject);
    }

    @PostMapping("/subjects/delete")
    public ResponseEntity<?> deleteSubject(@RequestBody Subject subject) {
        log.debug("REST request to delete Subject : {}", subject);
        Subject subjectOld = subjectRepository.findById(subject.getId()).get();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        subjectOld.setStatus(false);
        subjectOld.setUpdateDate(Instant.now());
        subjectOld.setUpdateName(userCreate.get().getLogin());
        subjectRepository.save(subjectOld);
        return ResponseEntity.ok().body(subjectOld);
    }

    @PostMapping("subjects/export")
    public ResponseEntity<?> export(@RequestBody SearchSubjectDTO searchSubjectDTO) throws Exception {
        List<SubjectDTO> listData = subjectService.exportData(searchSubjectDTO);
        List<ExcelColumn> lstColumn = buildColumnExport();
        String title = "Danh sách môn học";
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
        lstColumn.add(new ExcelColumn("code", "Mã môn học", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("name", "Tên môn học", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("className", "Lớp học", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("statusStr", "Trạng thái", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createDate", "Ngày tạo", ExcelColumn.ALIGN_MENT.LEFT));
        lstColumn.add(new ExcelColumn("createName", "Người tạo", ExcelColumn.ALIGN_MENT.LEFT));
        return lstColumn;
    }

    @GetMapping("/subjects/sample-file")
    public ResponseEntity<?> getSampleFile() throws IOException {
        log.debug("REST request to download file sample");
        try {
            ByteArrayInputStream bais = subjectService.getSampleFile();
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

    @PostMapping("/subjects/search")
    public ResponseEntity<?> search(
        @RequestBody SearchSubjectDTO searchSubjectDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = subjectService.search(searchSubjectDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/subjects/findByCode")
    public ResponseEntity<?> findByCode(@RequestBody String code) {
        Subject subject = subjectRepository.findByCode(code);
        return ResponseEntity.ok().body(subject);
    }

    @GetMapping("/subjects/findByClassroomCode/{code}")
    public ResponseEntity<?> findByClassroomCode(@PathVariable String code) {
        List<Subject> lstSubject = subjectRepository.findByClassCode(code);
        return ResponseEntity.ok().body(lstSubject);
    }

    @PostMapping("/subjects/importData")
    public ServiceResult<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("typeImport") Long typeImport)
        throws Exception {
        return subjectService.importSubject(file, typeImport);
    }

    @PostMapping("/subjects/exportTemplate")
    public ResponseEntity<?> exportTemplate() throws Exception {
        log.info("REST request to export template Teacher");
        byte[] fileData = subjectService.exportFileTemplate();
        String fileName = "DS_Monhoc" + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/subjects/exportDataErrors")
    public ResponseEntity<?> exportDataErrors(@RequestBody List<SubjectDTO> lstError) throws Exception {
        byte[] fileData = subjectService.exportExcelSubjectErrors(lstError);
        String fileName = "DS_MonHoc_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
