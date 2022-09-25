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

import javax.ws.rs.QueryParam;

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

    @GetMapping("/exam-users")
    public List<ExamUser> getAllExamUsers() {
        log.debug("REST request to get all ExamUsers");
        return examUserRepository.findAll();
    }

    @GetMapping("/exam-users/{id}")
    public ResponseEntity<?> getExamUser(@PathVariable Long id) {
        Optional<ExamUser> examUser = examUserRepository.findById(id);
        return ResponseEntity.ok().body(examUser.get());
    }

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
    public ResponseEntity<?> findByExamId(@PathVariable Long id,
                                          @RequestParam("studentCode") String studentCode) {
        ExamDTO examDTO = examService.dataExamStudent(id, studentCode);
        return ResponseEntity.ok().body(examDTO);
    }
}
