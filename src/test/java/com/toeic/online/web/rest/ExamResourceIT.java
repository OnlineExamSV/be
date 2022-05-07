package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.Exam;
import com.toeic.online.repository.ExamRepository;
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
 * Integration tests for the {@link ExamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExamResourceIT {

    private static final Instant DEFAULT_BEGIN_EXAM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BEGIN_EXAM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_DURATION_EXAM = 1;
    private static final Integer UPDATED_DURATION_EXAM = 2;

    private static final Instant DEFAULT_FINISH_EXAM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINISH_EXAM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_QUESTION_DATA = "AAAAAAAAAA";
    private static final String UPDATED_QUESTION_DATA = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Instant DEFAULT_CREATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CREATE_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UPDATE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_UPDATE_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/exams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExamMockMvc;

    private Exam exam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createEntity(EntityManager em) {
        Exam exam = new Exam()
            .beginExam(DEFAULT_BEGIN_EXAM)
            .durationExam(DEFAULT_DURATION_EXAM)
            .finishExam(DEFAULT_FINISH_EXAM)
            .questionData(DEFAULT_QUESTION_DATA)
            .subjectCode(DEFAULT_SUBJECT_CODE)
            .title(DEFAULT_TITLE)
            .status(DEFAULT_STATUS)
            .createDate(DEFAULT_CREATE_DATE)
            .createName(DEFAULT_CREATE_NAME)
            .updateDate(DEFAULT_UPDATE_DATE)
            .updateName(DEFAULT_UPDATE_NAME);
        return exam;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Exam createUpdatedEntity(EntityManager em) {
        Exam exam = new Exam()
            .beginExam(UPDATED_BEGIN_EXAM)
            .durationExam(UPDATED_DURATION_EXAM)
            .finishExam(UPDATED_FINISH_EXAM)
            .questionData(UPDATED_QUESTION_DATA)
            .subjectCode(UPDATED_SUBJECT_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .createDate(UPDATED_CREATE_DATE)
            .createName(UPDATED_CREATE_NAME)
            .updateDate(UPDATED_UPDATE_DATE)
            .updateName(UPDATED_UPDATE_NAME);
        return exam;
    }

    @BeforeEach
    public void initTest() {
        exam = createEntity(em);
    }

    @Test
    @Transactional
    void createExam() throws Exception {
        int databaseSizeBeforeCreate = examRepository.findAll().size();
        // Create the Exam
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isCreated());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeCreate + 1);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getBeginExam()).isEqualTo(DEFAULT_BEGIN_EXAM);
        assertThat(testExam.getDurationExam()).isEqualTo(DEFAULT_DURATION_EXAM);
        assertThat(testExam.getFinishExam()).isEqualTo(DEFAULT_FINISH_EXAM);
        assertThat(testExam.getQuestionData()).isEqualTo(DEFAULT_QUESTION_DATA);
        assertThat(testExam.getSubjectCode()).isEqualTo(DEFAULT_SUBJECT_CODE);
        assertThat(testExam.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testExam.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testExam.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testExam.getCreateName()).isEqualTo(DEFAULT_CREATE_NAME);
        assertThat(testExam.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
        assertThat(testExam.getUpdateName()).isEqualTo(DEFAULT_UPDATE_NAME);
    }

    @Test
    @Transactional
    void createExamWithExistingId() throws Exception {
        // Create the Exam with an existing ID
        exam.setId(1L);

        int databaseSizeBeforeCreate = examRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllExams() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        // Get all the examList
        restExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(exam.getId().intValue())))
            .andExpect(jsonPath("$.[*].beginExam").value(hasItem(DEFAULT_BEGIN_EXAM.toString())))
            .andExpect(jsonPath("$.[*].durationExam").value(hasItem(DEFAULT_DURATION_EXAM)))
            .andExpect(jsonPath("$.[*].finishExam").value(hasItem(DEFAULT_FINISH_EXAM.toString())))
            .andExpect(jsonPath("$.[*].questionData").value(hasItem(DEFAULT_QUESTION_DATA)))
            .andExpect(jsonPath("$.[*].subjectCode").value(hasItem(DEFAULT_SUBJECT_CODE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(DEFAULT_CREATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].createName").value(hasItem(DEFAULT_CREATE_NAME)))
            .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
            .andExpect(jsonPath("$.[*].updateName").value(hasItem(DEFAULT_UPDATE_NAME)));
    }

    @Test
    @Transactional
    void getExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        // Get the exam
        restExamMockMvc
            .perform(get(ENTITY_API_URL_ID, exam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(exam.getId().intValue()))
            .andExpect(jsonPath("$.beginExam").value(DEFAULT_BEGIN_EXAM.toString()))
            .andExpect(jsonPath("$.durationExam").value(DEFAULT_DURATION_EXAM))
            .andExpect(jsonPath("$.finishExam").value(DEFAULT_FINISH_EXAM.toString()))
            .andExpect(jsonPath("$.questionData").value(DEFAULT_QUESTION_DATA))
            .andExpect(jsonPath("$.subjectCode").value(DEFAULT_SUBJECT_CODE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.createDate").value(DEFAULT_CREATE_DATE.toString()))
            .andExpect(jsonPath("$.createName").value(DEFAULT_CREATE_NAME))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.updateName").value(DEFAULT_UPDATE_NAME));
    }

    @Test
    @Transactional
    void getNonExistingExam() throws Exception {
        // Get the exam
        restExamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();

        // Update the exam
        Exam updatedExam = examRepository.findById(exam.getId()).get();
        // Disconnect from session so that the updates on updatedExam are not directly saved in db
        em.detach(updatedExam);
        updatedExam
            .beginExam(UPDATED_BEGIN_EXAM)
            .durationExam(UPDATED_DURATION_EXAM)
            .finishExam(UPDATED_FINISH_EXAM)
            .questionData(UPDATED_QUESTION_DATA)
            .subjectCode(UPDATED_SUBJECT_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .createDate(UPDATED_CREATE_DATE)
            .createName(UPDATED_CREATE_NAME)
            .updateDate(UPDATED_UPDATE_DATE)
            .updateName(UPDATED_UPDATE_NAME);

        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getBeginExam()).isEqualTo(UPDATED_BEGIN_EXAM);
        assertThat(testExam.getDurationExam()).isEqualTo(UPDATED_DURATION_EXAM);
        assertThat(testExam.getFinishExam()).isEqualTo(UPDATED_FINISH_EXAM);
        assertThat(testExam.getQuestionData()).isEqualTo(UPDATED_QUESTION_DATA);
        assertThat(testExam.getSubjectCode()).isEqualTo(UPDATED_SUBJECT_CODE);
        assertThat(testExam.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testExam.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testExam.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testExam.getCreateName()).isEqualTo(UPDATED_CREATE_NAME);
        assertThat(testExam.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testExam.getUpdateName()).isEqualTo(UPDATED_UPDATE_NAME);
    }

    @Test
    @Transactional
    void putNonExistingExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, exam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateExamWithPatch() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .beginExam(UPDATED_BEGIN_EXAM)
            .durationExam(UPDATED_DURATION_EXAM)
            .finishExam(UPDATED_FINISH_EXAM)
            .questionData(UPDATED_QUESTION_DATA)
            .subjectCode(UPDATED_SUBJECT_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .createDate(UPDATED_CREATE_DATE)
            .updateDate(UPDATED_UPDATE_DATE);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getBeginExam()).isEqualTo(UPDATED_BEGIN_EXAM);
        assertThat(testExam.getDurationExam()).isEqualTo(UPDATED_DURATION_EXAM);
        assertThat(testExam.getFinishExam()).isEqualTo(UPDATED_FINISH_EXAM);
        assertThat(testExam.getQuestionData()).isEqualTo(UPDATED_QUESTION_DATA);
        assertThat(testExam.getSubjectCode()).isEqualTo(UPDATED_SUBJECT_CODE);
        assertThat(testExam.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testExam.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testExam.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testExam.getCreateName()).isEqualTo(DEFAULT_CREATE_NAME);
        assertThat(testExam.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testExam.getUpdateName()).isEqualTo(DEFAULT_UPDATE_NAME);
    }

    @Test
    @Transactional
    void fullUpdateExamWithPatch() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeUpdate = examRepository.findAll().size();

        // Update the exam using partial update
        Exam partialUpdatedExam = new Exam();
        partialUpdatedExam.setId(exam.getId());

        partialUpdatedExam
            .beginExam(UPDATED_BEGIN_EXAM)
            .durationExam(UPDATED_DURATION_EXAM)
            .finishExam(UPDATED_FINISH_EXAM)
            .questionData(UPDATED_QUESTION_DATA)
            .subjectCode(UPDATED_SUBJECT_CODE)
            .title(UPDATED_TITLE)
            .status(UPDATED_STATUS)
            .createDate(UPDATED_CREATE_DATE)
            .createName(UPDATED_CREATE_NAME)
            .updateDate(UPDATED_UPDATE_DATE)
            .updateName(UPDATED_UPDATE_NAME);

        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExam))
            )
            .andExpect(status().isOk());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
        Exam testExam = examList.get(examList.size() - 1);
        assertThat(testExam.getBeginExam()).isEqualTo(UPDATED_BEGIN_EXAM);
        assertThat(testExam.getDurationExam()).isEqualTo(UPDATED_DURATION_EXAM);
        assertThat(testExam.getFinishExam()).isEqualTo(UPDATED_FINISH_EXAM);
        assertThat(testExam.getQuestionData()).isEqualTo(UPDATED_QUESTION_DATA);
        assertThat(testExam.getSubjectCode()).isEqualTo(UPDATED_SUBJECT_CODE);
        assertThat(testExam.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testExam.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testExam.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testExam.getCreateName()).isEqualTo(UPDATED_CREATE_NAME);
        assertThat(testExam.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testExam.getUpdateName()).isEqualTo(UPDATED_UPDATE_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, exam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(exam))
            )
            .andExpect(status().isBadRequest());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExam() throws Exception {
        int databaseSizeBeforeUpdate = examRepository.findAll().size();
        exam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(exam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Exam in the database
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteExam() throws Exception {
        // Initialize the database
        examRepository.saveAndFlush(exam);

        int databaseSizeBeforeDelete = examRepository.findAll().size();

        // Delete the exam
        restExamMockMvc
            .perform(delete(ENTITY_API_URL_ID, exam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Exam> examList = examRepository.findAll();
        assertThat(examList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
