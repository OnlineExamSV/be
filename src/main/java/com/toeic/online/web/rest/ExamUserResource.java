package com.toeic.online.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toeic.online.domain.ExamUser;
import com.toeic.online.repository.ExamUserRepository;
import com.toeic.online.service.ExamService;
import com.toeic.online.service.dto.ExamDTO;
import com.toeic.online.service.dto.ExamStudentDTO;
import com.toeic.online.service.dto.QuestionDTO;
import com.toeic.online.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.toeic.online.domain.ExamUser}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ExamUserResource {

    private final Logger log = LoggerFactory.getLogger(ExamUserResource.class);

    private static final String ENTITY_NAME = "examUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExamUserRepository examUserRepository;

    private final ExamService examService;

    public ExamUserResource(ExamUserRepository examUserRepository, ExamService examService) {
        this.examUserRepository = examUserRepository;
        this.examService = examService;
    }

    /**
     * {@code POST  /exam-users} : Create a new examUser.
     *
     * @param examUserDTO the examUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new examUser, or with status {@code 400 (Bad Request)} if the examUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/exam-users")
    public ResponseEntity<ExamUser> createExamUser(@RequestBody ExamStudentDTO examUserDTO)
        throws URISyntaxException, JsonProcessingException {
        log.debug("REST request to save ExamUser : {}", examUserDTO);
        if (examUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new examUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        // Convert dto to entity
        ExamUser examUser = new ExamUser();
        examUser.setId(examUserDTO.getId());
        examUser.setStudentCode(examUserDTO.getStudentCode());
        examUser.setExamId(examUserDTO.getExamId());
        examUser.setTotalPoint(examUserDTO.getTotalPoint());
        examUser.setTimeFinish(examUserDTO.getTimeFinish());
        examUser.setTimeRemaining(examUserDTO.getTimeRemaining());
        examUser.setTimeStart(examUserDTO.getTimeStart());
        ObjectMapper mapper = new ObjectMapper();
        String answerSheetConvertToJson = mapper.writeValueAsString(examUserDTO.getLstQuestion());
        examUser.setAnswerSheet(answerSheetConvertToJson);
        ExamUser result = examUserRepository.save(examUser);
        return ResponseEntity
            .created(new URI("/api/exam-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /exam-users/:id} : Updates an existing examUser.
     *
     * @param id the id of the examUser to save.
     * @param examUser the examUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examUser,
     * or with status {@code 400 (Bad Request)} if the examUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the examUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/exam-users/{id}")
    public ResponseEntity<ExamUser> updateExamUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExamUser examUser
    ) throws URISyntaxException {
        log.debug("REST request to update ExamUser : {}, {}", id, examUser);
        if (examUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ExamUser result = examUserRepository.save(examUser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examUser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /exam-users/:id} : Partial updates given fields of an existing examUser, field will ignore if it is null
     *
     * @param id the id of the examUser to save.
     * @param examUser the examUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated examUser,
     * or with status {@code 400 (Bad Request)} if the examUser is not valid,
     * or with status {@code 404 (Not Found)} if the examUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the examUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/exam-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ExamUser> partialUpdateExamUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ExamUser examUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update ExamUser partially : {}, {}", id, examUser);
        if (examUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, examUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ExamUser> result = examUserRepository
            .findById(examUser.getId())
            .map(
                existingExamUser -> {
                    if (examUser.getStudentCode() != null) {
                        existingExamUser.setStudentCode(examUser.getStudentCode());
                    }
                    if (examUser.getExamId() != null) {
                        existingExamUser.setExamId(examUser.getExamId());
                    }
                    if (examUser.getTotalPoint() != null) {
                        existingExamUser.setTotalPoint(examUser.getTotalPoint());
                    }
                    if (examUser.getAnswerSheet() != null) {
                        existingExamUser.setAnswerSheet(examUser.getAnswerSheet());
                    }
                    if (examUser.getTimeStart() != null) {
                        existingExamUser.setTimeStart(examUser.getTimeStart());
                    }
                    if (examUser.getTimeFinish() != null) {
                        existingExamUser.setTimeFinish(examUser.getTimeFinish());
                    }
                    if (examUser.getTimeRemaining() != null) {
                        existingExamUser.setTimeRemaining(examUser.getTimeRemaining());
                    }

                    return existingExamUser;
                }
            )
            .map(examUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, examUser.getId().toString())
        );
    }

    /**
     * {@code GET  /exam-users} : get all the examUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of examUsers in body.
     */
    @GetMapping("/exam-users")
    public List<ExamUser> getAllExamUsers() {
        log.debug("REST request to get all ExamUsers");
        return examUserRepository.findAll();
    }

    /**
     * {@code GET  /exam-users/:id} : get the "id" examUser.
     *
     * @param id the id of the examUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the examUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/exam-users/{id}")
    public ResponseEntity<?> getExamUser(@PathVariable Long id) {
        Optional<ExamUser> examUser = examUserRepository.findById(id);
        return ResponseEntity.ok().body(examUser.get());
    }

    /**
     * {@code DELETE  /exam-users/:id} : delete the "id" examUser.
     *
     * @param id the id of the examUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/exam-users/{id}")
    public ResponseEntity<Void> deleteExamUser(@PathVariable Long id) {
        log.debug("REST request to delete ExamUser : {}", id);
        examUserRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    // Lấy thông tin data của bài thi
    @GetMapping("/exam-users/findByExamId/{id}")
    public ResponseEntity<?> findByExamId(@PathVariable Long id) {
        ExamDTO examDTO = examService.dataExamStudent(id);
        return ResponseEntity.ok().body(examDTO);
    }
}
