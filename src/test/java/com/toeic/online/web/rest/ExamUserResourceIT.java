package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.ExamUser;
import com.toeic.online.repository.ExamUserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExamUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamUserResourceIT {

    private static final String DEFAULT_STUDENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_EXAM_ID = 1L;
    private static final Long UPDATED_EXAM_ID = 2L;

    private static final Float DEFAULT_TOTAL_POINT = 1F;
    private static final Float UPDATED_TOTAL_POINT = 2F;

    private static final String DEFAULT_ANSWER_SHEET = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER_SHEET = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME_START = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_START = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_TIME_FINISH = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME_FINISH = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_TIME_REMAINING = 1;
    private static final Integer UPDATED_TIME_REMAINING = 2;

    private static final String ENTITY_API_URL = "/api/exam-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExamUserRepository examUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamUserMockMvc;

    private ExamUser examUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamUser createEntity(EntityManager em) {
        ExamUser examUser = new ExamUser()
            .studentCode(DEFAULT_STUDENT_CODE)
            .examId(DEFAULT_EXAM_ID)
            .totalPoint(DEFAULT_TOTAL_POINT)
            .answerSheet(DEFAULT_ANSWER_SHEET)
            .timeStart(DEFAULT_TIME_START)
            .timeFinish(DEFAULT_TIME_FINISH)
            .timeRemaining(DEFAULT_TIME_REMAINING);
        return examUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExamUser createUpdatedEntity(EntityManager em) {
        ExamUser examUser = new ExamUser()
            .studentCode(UPDATED_STUDENT_CODE)
            .examId(UPDATED_EXAM_ID)
            .totalPoint(UPDATED_TOTAL_POINT)
            .answerSheet(UPDATED_ANSWER_SHEET)
            .timeStart(UPDATED_TIME_START)
            .timeFinish(UPDATED_TIME_FINISH)
            .timeRemaining(UPDATED_TIME_REMAINING);
        return examUser;
    }

    @BeforeEach
    public void initTest() {
        examUser = createEntity(em);
    }

    @Test
    @Transactional
    void createExamUser() throws Exception {
        int databaseSizeBeforeCreate = examUserRepository.findAll().size();
        // Create the ExamUser
        restExamUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examUser)))
            .andExpect(status().isCreated());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeCreate + 1);
        ExamUser testExamUser = examUserList.get(examUserList.size() - 1);
        assertThat(testExamUser.getStudentCode()).isEqualTo(DEFAULT_STUDENT_CODE);
        assertThat(testExamUser.getExamId()).isEqualTo(DEFAULT_EXAM_ID);
        assertThat(testExamUser.getTotalPoint()).isEqualTo(DEFAULT_TOTAL_POINT);
        assertThat(testExamUser.getAnswerSheet()).isEqualTo(DEFAULT_ANSWER_SHEET);
        assertThat(testExamUser.getTimeStart()).isEqualTo(DEFAULT_TIME_START);
        assertThat(testExamUser.getTimeFinish()).isEqualTo(DEFAULT_TIME_FINISH);
        assertThat(testExamUser.getTimeRemaining()).isEqualTo(DEFAULT_TIME_REMAINING);
    }

    @Test
    @Transactional
    void createExamUserWithExistingId() throws Exception {
        // Create the ExamUser with an existing ID
        examUser.setId(1L);

        int databaseSizeBeforeCreate = examUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examUser)))
            .andExpect(status().isBadRequest());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExamUsers() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        // Get all the examUserList
        restExamUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(examUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].studentCode").value(hasItem(DEFAULT_STUDENT_CODE)))
            .andExpect(jsonPath("$.[*].examId").value(hasItem(DEFAULT_EXAM_ID.intValue())))
            .andExpect(jsonPath("$.[*].totalPoint").value(hasItem(DEFAULT_TOTAL_POINT.doubleValue())))
            .andExpect(jsonPath("$.[*].answerSheet").value(hasItem(DEFAULT_ANSWER_SHEET)))
            .andExpect(jsonPath("$.[*].timeStart").value(hasItem(DEFAULT_TIME_START.toString())))
            .andExpect(jsonPath("$.[*].timeFinish").value(hasItem(DEFAULT_TIME_FINISH.toString())))
            .andExpect(jsonPath("$.[*].timeRemaining").value(hasItem(DEFAULT_TIME_REMAINING)));
    }

    @Test
    @Transactional
    void getExamUser() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        // Get the examUser
        restExamUserMockMvc
            .perform(get(ENTITY_API_URL_ID, examUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(examUser.getId().intValue()))
            .andExpect(jsonPath("$.studentCode").value(DEFAULT_STUDENT_CODE))
            .andExpect(jsonPath("$.examId").value(DEFAULT_EXAM_ID.intValue()))
            .andExpect(jsonPath("$.totalPoint").value(DEFAULT_TOTAL_POINT.doubleValue()))
            .andExpect(jsonPath("$.answerSheet").value(DEFAULT_ANSWER_SHEET))
            .andExpect(jsonPath("$.timeStart").value(DEFAULT_TIME_START.toString()))
            .andExpect(jsonPath("$.timeFinish").value(DEFAULT_TIME_FINISH.toString()))
            .andExpect(jsonPath("$.timeRemaining").value(DEFAULT_TIME_REMAINING));
    }

    @Test
    @Transactional
    void getNonExistingExamUser() throws Exception {
        // Get the examUser
        restExamUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExamUser() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();

        // Update the examUser
        ExamUser updatedExamUser = examUserRepository.findById(examUser.getId()).get();
        // Disconnect from session so that the updates on updatedExamUser are not directly saved in db
        em.detach(updatedExamUser);
        updatedExamUser
            .studentCode(UPDATED_STUDENT_CODE)
            .examId(UPDATED_EXAM_ID)
            .totalPoint(UPDATED_TOTAL_POINT)
            .answerSheet(UPDATED_ANSWER_SHEET)
            .timeStart(UPDATED_TIME_START)
            .timeFinish(UPDATED_TIME_FINISH)
            .timeRemaining(UPDATED_TIME_REMAINING);

        restExamUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExamUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExamUser))
            )
            .andExpect(status().isOk());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
        ExamUser testExamUser = examUserList.get(examUserList.size() - 1);
        assertThat(testExamUser.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
        assertThat(testExamUser.getExamId()).isEqualTo(UPDATED_EXAM_ID);
        assertThat(testExamUser.getTotalPoint()).isEqualTo(UPDATED_TOTAL_POINT);
        assertThat(testExamUser.getAnswerSheet()).isEqualTo(UPDATED_ANSWER_SHEET);
        assertThat(testExamUser.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testExamUser.getTimeFinish()).isEqualTo(UPDATED_TIME_FINISH);
        assertThat(testExamUser.getTimeRemaining()).isEqualTo(UPDATED_TIME_REMAINING);
    }

    @Test
    @Transactional
    void putNonExistingExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, examUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(examUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(examUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamUserWithPatch() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();

        // Update the examUser using partial update
        ExamUser partialUpdatedExamUser = new ExamUser();
        partialUpdatedExamUser.setId(examUser.getId());

        partialUpdatedExamUser.examId(UPDATED_EXAM_ID).totalPoint(UPDATED_TOTAL_POINT).timeStart(UPDATED_TIME_START);

        restExamUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamUser))
            )
            .andExpect(status().isOk());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
        ExamUser testExamUser = examUserList.get(examUserList.size() - 1);
        assertThat(testExamUser.getStudentCode()).isEqualTo(DEFAULT_STUDENT_CODE);
        assertThat(testExamUser.getExamId()).isEqualTo(UPDATED_EXAM_ID);
        assertThat(testExamUser.getTotalPoint()).isEqualTo(UPDATED_TOTAL_POINT);
        assertThat(testExamUser.getAnswerSheet()).isEqualTo(DEFAULT_ANSWER_SHEET);
        assertThat(testExamUser.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testExamUser.getTimeFinish()).isEqualTo(DEFAULT_TIME_FINISH);
        assertThat(testExamUser.getTimeRemaining()).isEqualTo(DEFAULT_TIME_REMAINING);
    }

    @Test
    @Transactional
    void fullUpdateExamUserWithPatch() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();

        // Update the examUser using partial update
        ExamUser partialUpdatedExamUser = new ExamUser();
        partialUpdatedExamUser.setId(examUser.getId());

        partialUpdatedExamUser
            .studentCode(UPDATED_STUDENT_CODE)
            .examId(UPDATED_EXAM_ID)
            .totalPoint(UPDATED_TOTAL_POINT)
            .answerSheet(UPDATED_ANSWER_SHEET)
            .timeStart(UPDATED_TIME_START)
            .timeFinish(UPDATED_TIME_FINISH)
            .timeRemaining(UPDATED_TIME_REMAINING);

        restExamUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExamUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExamUser))
            )
            .andExpect(status().isOk());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
        ExamUser testExamUser = examUserList.get(examUserList.size() - 1);
        assertThat(testExamUser.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
        assertThat(testExamUser.getExamId()).isEqualTo(UPDATED_EXAM_ID);
        assertThat(testExamUser.getTotalPoint()).isEqualTo(UPDATED_TOTAL_POINT);
        assertThat(testExamUser.getAnswerSheet()).isEqualTo(UPDATED_ANSWER_SHEET);
        assertThat(testExamUser.getTimeStart()).isEqualTo(UPDATED_TIME_START);
        assertThat(testExamUser.getTimeFinish()).isEqualTo(UPDATED_TIME_FINISH);
        assertThat(testExamUser.getTimeRemaining()).isEqualTo(UPDATED_TIME_REMAINING);
    }

    @Test
    @Transactional
    void patchNonExistingExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, examUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(examUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExamUser() throws Exception {
        int databaseSizeBeforeUpdate = examUserRepository.findAll().size();
        examUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(examUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ExamUser in the database
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExamUser() throws Exception {
        // Initialize the database
        examUserRepository.saveAndFlush(examUser);

        int databaseSizeBeforeDelete = examUserRepository.findAll().size();

        // Delete the examUser
        restExamUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, examUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ExamUser> examUserList = examUserRepository.findAll();
        assertThat(examUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
