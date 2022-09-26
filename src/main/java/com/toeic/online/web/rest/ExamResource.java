package com.toeic.online.web.rest;

import com.toeic.online.domain.Classroom;
import com.toeic.online.domain.Exam;
import com.toeic.online.domain.User;
import com.toeic.online.repository.ExamRepository;
import com.toeic.online.service.ExamService;
import com.toeic.online.service.UserService;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import com.toeic.online.service.dto.ExamDTO;
import com.toeic.online.service.dto.StudentDTO;
import com.toeic.online.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
@Transactional
public class ExamResource {

    private final Logger log = LoggerFactory.getLogger(ExamResource.class);

    private static final String ENTITY_NAME = "exam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExamRepository examRepository;

    private final UserService userService;

    private final ExamService examService;

    public ExamResource(ExamRepository examRepository, UserService userService, ExamService examService) {
        this.examRepository = examRepository;
        this.userService = userService;
        this.examService = examService;
    }

    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody ExamDTO examDTO) throws URISyntaxException {
        Exam exam = this.convertExamt(examDTO);
        Optional<User> userLogin = userService.getUserWithAuthorities();
        if (exam.getId() == null) {
            exam.setCreateName(userLogin.get().getLogin());
            exam.setCreateDate(new Date().toInstant());
        } else {
            Exam examOld = examRepository.findById(exam.getId()).get();
            exam.setCreateDate(examOld.getCreateDate());
            exam.setCreateName(examOld.getCreateName());
            exam.setUpdateDate(new Date().toInstant());
            exam.setUpdateName(userLogin.get().getLogin());
        }
        exam.setStatus(true);
        Exam result = examRepository.save(exam);
        return ResponseEntity
            .created(new URI("/api/exams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /exams/:id} : Updates an existing exam.
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/exams/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable(value = "id", required = false) final Long id, @RequestBody Exam exam)
        throws URISyntaxException {
        log.debug("REST request to update Exam : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Exam result = examRepository.save(exam);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /exams/:id} : Partial updates given fields of an existing exam, field will ignore if it is null
     *
     * @param id the id of the exam to save.
     * @param exam the exam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated exam,
     * or with status {@code 400 (Bad Request)} if the exam is not valid,
     * or with status {@code 404 (Not Found)} if the exam is not found,
     * or with status {@code 500 (Internal Server Error)} if the exam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/exams/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Exam> partialUpdateExam(@PathVariable(value = "id", required = false) final Long id, @RequestBody Exam exam)
        throws URISyntaxException {
        log.debug("REST request to partial update Exam partially : {}, {}", id, exam);
        if (exam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, exam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!examRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Exam> result = examRepository
            .findById(exam.getId())
            .map(
                existingExam -> {
                    if (exam.getBeginExam() != null) {
                        existingExam.setBeginExam(exam.getBeginExam());
                    }
                    if (exam.getDurationExam() != null) {
                        existingExam.setDurationExam(exam.getDurationExam());
                    }
                    if (exam.getFinishExam() != null) {
                        existingExam.setFinishExam(exam.getFinishExam());
                    }
                    if (exam.getQuestionData() != null) {
                        existingExam.setQuestionData(exam.getQuestionData());
                    }
                    if (exam.getSubjectCode() != null) {
                        existingExam.setSubjectCode(exam.getSubjectCode());
                    }
                    if (exam.getTitle() != null) {
                        existingExam.setTitle(exam.getTitle());
                    }
                    if (exam.getStatus() != null) {
                        existingExam.setStatus(exam.getStatus());
                    }
                    if (exam.getCreateDate() != null) {
                        existingExam.setCreateDate(exam.getCreateDate());
                    }
                    if (exam.getCreateName() != null) {
                        existingExam.setCreateName(exam.getCreateName());
                    }
                    if (exam.getUpdateDate() != null) {
                        existingExam.setUpdateDate(exam.getUpdateDate());
                    }
                    if (exam.getUpdateName() != null) {
                        existingExam.setUpdateName(exam.getUpdateName());
                    }

                    return existingExam;
                }
            )
            .map(examRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, exam.getId().toString())
        );
    }

    /**
     * {@code GET  /exams} : get all the exams.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of exams in body.
     */
    @GetMapping("/exams")
    public List<Exam> getAllExams() {
        log.debug("REST request to get all Exams");
        return examRepository.findAll();
    }

    /**
     * {@code GET  /exams/:id} : get the "id" exam.
     *
     * @param id the id of the exam to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the exam, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/exams/{id}")
    public ResponseEntity<?> getExam(@PathVariable Long id) {
//        log.debug("REST request to get Exam : {}", id);
//        Optional<Exam> exam = examRepository.findById(id);
//        return ResponseUtil.wrapOrNotFound(exam);
        ExamDTO examDTO = examService.findById(id);
        return ResponseEntity.ok().body(examDTO);
    }

    /**
     * {@code DELETE  /exams/:id} : delete the "id" exam.
     *
     * @param id the id of the exam to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/exams/{id}")
    public ResponseEntity<?> deleteExam(@RequestBody Exam exam) {
//        log.debug("REST request to delete Exam : {}", id);
//        examRepository.deleteById(id);
//        return ResponseEntity
//            .noContent()
//            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
//            .build();
        Exam examOld = examRepository.findById(exam.getId()).get();
        Optional<User> userCreate = userService.getUserWithAuthorities();
        examOld.setStatus(false);
        examOld.setUpdateDate(Instant.now());
        examOld.setUpdateName(userCreate.get().getLogin());
        examRepository.save(examOld);
        return ResponseEntity.ok().body(examOld);
    }

    // Mapper examDTO => exam
    public Exam convertExamt(ExamDTO examDTO) {
        Exam exam = new Exam();
        exam.setBeginExam(examDTO.getBeginExam());
        exam.setFinishExam(examDTO.getFinishExam());
        exam.setDurationExam(examDTO.getDurationExam());
        exam.setSubjectCode(examDTO.getSubjectCode());
        exam.setQuestionData(examDTO.getQuestionData());
        exam.setTitle(examDTO.getTitle());
        return exam;
    }

    @PostMapping("/exams/search")
    public ResponseEntity<?> search(
        @RequestBody ClassroomSearchDTO classroomSearchDTO,
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "page-size", required = false, defaultValue = "10") Integer pageSize
    ) {
        Map<String, Object> result = examService.search(classroomSearchDTO, page, pageSize);
        return ResponseEntity.ok().body(result);
    }

    // List bài thi theo mã sv
    @GetMapping("/exams/getListByStudentCode/{studentCode}")
    public ResponseEntity<?> getListExamsByStudentCode(@PathVariable String studentCode) {
        List<ExamDTO> list = examService.getListExamByStudentCode(studentCode);
        return ResponseEntity.ok().body(list);
    }

    // Danh sách bài thi theo giáo viên
    @GetMapping("/exams/findByTeacherCode/{teacherCode}")
    public ResponseEntity<?> getListExamByTeacherCode(@PathVariable String teacherCode) {
        List<Exam> lst = examRepository.getListExamByTeacherCode(teacherCode);
        return ResponseEntity.ok().body(lst);
    }

    // Danh sách điểm của sv theo bài thi
    @GetMapping("/exams/getPointExamStudent/{examId}")
    public ResponseEntity<?> getPointExamStudent(@PathVariable Long examId) {
        List<StudentDTO> lst = examService.getPointExamStudent(examId);
        return ResponseEntity.ok().body(lst);
    }

    @GetMapping("/exams/findById/{id}")
    public ResponseEntity<?> findByExamId(@PathVariable Long id){
        ExamDTO examDTO = examService.findById(id);
        return ResponseEntity.ok().body(examDTO);
     }
}
