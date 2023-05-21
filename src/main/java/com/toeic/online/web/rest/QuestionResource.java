package com.toeic.online.web.rest;

import com.toeic.online.commons.FileExportUtil;
import com.toeic.online.constant.AppConstants;
import com.toeic.online.domain.Question;
import com.toeic.online.domain.User;
import com.toeic.online.repository.QuestionRepository;
import com.toeic.online.service.QuestionService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.QuestionDTO;
import com.toeic.online.service.dto.SearchQuestionDTO;
import com.toeic.online.service.dto.ServiceResult;
import com.toeic.online.service.dto.StudentDTO;
import com.toeic.online.web.rest.errors.BadRequestAlertException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.toeic.online.domain.Question}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class QuestionResource {

    private final Logger log = LoggerFactory.getLogger(QuestionResource.class);

    private static final String ENTITY_NAME = "question";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final QuestionRepository questionRepository;

    private final UserService userService;

    private final QuestionService questionService;

    @Autowired
    private FileExportUtil fileExportUtil;

    public QuestionResource(QuestionRepository questionRepository, UserService userService, QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.questionService = questionService;
    }

    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) throws URISyntaxException {
        Optional<User> userLogin = userService.getUserWithAuthorities();
        if (question.getId() == null) {
            question.setCreateDate(Instant.now());
            question.setCreateName(userLogin.get().getLogin());
        } else {
            question.setUpdateDate(Instant.now());
            question.setUpdateName(userLogin.get().getLogin());
        }
        question.setStatus(true);
        Question result = questionRepository.save(question);
        return ResponseEntity
            .created(new URI("/api/questions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<Question> updateQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Question question
    ) throws URISyntaxException {
        log.debug("REST request to update Question : {}, {}", id, question);
        if (question.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, question.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Question result = questionRepository.save(question);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, question.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/questions/{id}", consumes = {"application/json", "application/merge-patch+json"})
    public ResponseEntity<Question> partialUpdateQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Question question
    ) throws URISyntaxException {
        log.debug("REST request to partial update Question partially : {}, {}", id, question);
        if (question.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, question.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!questionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Question> result = questionRepository
            .findById(question.getId())
            .map(
                existingQuestion -> {
                    if (question.getQuestionType() != null) {
                        existingQuestion.setQuestionType(question.getQuestionType());
                    }
                    if (question.getQuestionText() != null) {
                        existingQuestion.setQuestionText(question.getQuestionText());
                    }
                    if (question.getSubjectCode() != null) {
                        existingQuestion.setSubjectCode(question.getSubjectCode());
                    }
                    if (question.getLevel() != null) {
                        existingQuestion.setLevel(question.getLevel());
                    }
                    if (question.getPoint() != null) {
                        existingQuestion.setPoint(question.getPoint());
                    }
                    if (question.getStatus() != null) {
                        existingQuestion.setStatus(question.getStatus());
                    }
                    if (question.getCreateDate() != null) {
                        existingQuestion.setCreateDate(question.getCreateDate());
                    }
                    if (question.getCreateName() != null) {
                        existingQuestion.setCreateName(question.getCreateName());
                    }
                    if (question.getUpdateDate() != null) {
                        existingQuestion.setUpdateDate(question.getUpdateDate());
                    }
                    if (question.getUpdateName() != null) {
                        existingQuestion.setUpdateName(question.getUpdateName());
                    }

                    return existingQuestion;
                }
            )
            .map(questionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, question.getId().toString())
        );
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        log.debug("REST request to get all Questions");
        return questionRepository.findAll();
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        log.debug("REST request to get Question : {}", id);
        Optional<Question> question = questionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(question);
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.debug("REST request to delete Question : {}", id);
        questionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/questions/search")
    public ResponseEntity<?> search(
        @RequestBody SearchQuestionDTO searchQuestionDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = questionService.search(searchQuestionDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/questions/findByQuestion")
    public ResponseEntity<?> findByQuestionId(@RequestBody Long id) {
        QuestionDTO questionDTO = questionService.findById(id);
        return ResponseEntity.ok().body(questionDTO);
    }

    // Tìm kiếm các các câu hỏi theo subjectCode
    @PostMapping("/questions/findBySubjectCode")
    public ResponseEntity<?> findBySubjectCode(@RequestBody SearchQuestionDTO searchQuestionDTO) {
        List<QuestionDTO> lstQuestion = questionService.export(searchQuestionDTO);
        return ResponseEntity.ok().body(lstQuestion);
    }

    @PostMapping("/questions/exportTemplate")
    public ResponseEntity<?> exportTemplate() throws Exception {
        log.info("REST request to export template Teacher");
        byte[] fileData = questionService.exportFileTemplate();
        String fileName = "DS_SV" + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }

    @PostMapping("/questions/importData")
    public ServiceResult<?> importData(@RequestParam("file") MultipartFile file)
        throws Exception {
        return questionService.importData(file);
    }

    @PostMapping("/questions/exportDataErrors")
    public ResponseEntity<?> exportDataErrors(@RequestBody List<QuestionDTO> lstError) throws Exception {
        byte[] fileData = questionService.exportExcelQuestionErrors(lstError);
        String fileName = "DS_Cau_Hoi_errors" + AppConstants.DOT + AppConstants.EXTENSION_XLSX;
        return fileExportUtil.responseFileExportWithUtf8FileName(fileData, fileName, AppConstants.MIME_TYPE_XLSX);
    }
}
