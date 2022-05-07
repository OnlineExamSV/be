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

    /**
     * {@code POST  /questions} : Create a new question.
     *
     * @param question the question to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new question, or with status {@code 400 (Bad Request)} if the question has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) throws URISyntaxException {
        //        log.debug("REST request to save Question : {}", question);
        //        if (question.getId() != null) {
        //            throw new BadRequestAlertException("A new question cannot already have an ID", ENTITY_NAME, "idexists");
        //        }
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

    /**
     * {@code PUT  /questions/:id} : Updates an existing question.
     *
     * @param id the id of the question to save.
     * @param question the question to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated question,
     * or with status {@code 400 (Bad Request)} if the question is not valid,
     * or with status {@code 500 (Internal Server Error)} if the question couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
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

    /**
     * {@code PATCH  /questions/:id} : Partial updates given fields of an existing question, field will ignore if it is null
     *
     * @param id the id of the question to save.
     * @param question the question to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated question,
     * or with status {@code 400 (Bad Request)} if the question is not valid,
     * or with status {@code 404 (Not Found)} if the question is not found,
     * or with status {@code 500 (Internal Server Error)} if the question couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/questions/{id}", consumes = { "application/json", "application/merge-patch+json" })
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

    /**
     * {@code GET  /questions} : get all the questions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of questions in body.
     */
    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        log.debug("REST request to get all Questions");
        return questionRepository.findAll();
    }

    /**
     * {@code GET  /questions/:id} : get the "id" question.
     *
     * @param id the id of the question to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the question, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestion(@PathVariable Long id) {
        log.debug("REST request to get Question : {}", id);
        Optional<Question> question = questionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(question);
    }

    /**
     * {@code DELETE  /questions/:id} : delete the "id" question.
     *
     * @param id the id of the question to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
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
}
