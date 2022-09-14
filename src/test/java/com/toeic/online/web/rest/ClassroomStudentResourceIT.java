package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.ClassroomStudent;
import com.toeic.online.repository.ClassroomStudentRepository;
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
 * Integration tests for the {@link ClassroomStudentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClassroomStudentResourceIT {

    private static final String DEFAULT_CLASS_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CLASS_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_STUDENT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_STUDENT_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/classroom-students";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClassroomStudentRepository classroomStudentRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClassroomStudentMockMvc;

    private ClassroomStudent classroomStudent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassroomStudent createEntity(EntityManager em) {
        ClassroomStudent classroomStudent = new ClassroomStudent().classCode(DEFAULT_CLASS_CODE).studentCode(DEFAULT_STUDENT_CODE);
        return classroomStudent;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ClassroomStudent createUpdatedEntity(EntityManager em) {
        ClassroomStudent classroomStudent = new ClassroomStudent().classCode(UPDATED_CLASS_CODE).studentCode(UPDATED_STUDENT_CODE);
        return classroomStudent;
    }

    @BeforeEach
    public void initTest() {
        classroomStudent = createEntity(em);
    }

    @Test
    @Transactional
    void createClassroomStudent() throws Exception {
        int databaseSizeBeforeCreate = classroomStudentRepository.findAll().size();
        // Create the ClassroomStudent
        restClassroomStudentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isCreated());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeCreate + 1);
        ClassroomStudent testClassroomStudent = classroomStudentList.get(classroomStudentList.size() - 1);
        assertThat(testClassroomStudent.getClassCode()).isEqualTo(DEFAULT_CLASS_CODE);
        assertThat(testClassroomStudent.getStudentCode()).isEqualTo(DEFAULT_STUDENT_CODE);
    }

    @Test
    @Transactional
    void createClassroomStudentWithExistingId() throws Exception {
        // Create the ClassroomStudent with an existing ID
        classroomStudent.setId(1L);

        int databaseSizeBeforeCreate = classroomStudentRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClassroomStudentMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClassroomStudents() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        // Get all the classroomStudentList
        restClassroomStudentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(classroomStudent.getId().intValue())))
            .andExpect(jsonPath("$.[*].classCode").value(hasItem(DEFAULT_CLASS_CODE)))
            .andExpect(jsonPath("$.[*].studentCode").value(hasItem(DEFAULT_STUDENT_CODE)));
    }

    @Test
    @Transactional
    void getClassroomStudent() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        // Get the classroomStudent
        restClassroomStudentMockMvc
            .perform(get(ENTITY_API_URL_ID, classroomStudent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(classroomStudent.getId().intValue()))
            .andExpect(jsonPath("$.classCode").value(DEFAULT_CLASS_CODE))
            .andExpect(jsonPath("$.studentCode").value(DEFAULT_STUDENT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingClassroomStudent() throws Exception {
        // Get the classroomStudent
        restClassroomStudentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClassroomStudent() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();

        // Update the classroomStudent
        ClassroomStudent updatedClassroomStudent = classroomStudentRepository.findById(classroomStudent.getId()).get();
        // Disconnect from session so that the updates on updatedClassroomStudent are not directly saved in db
        em.detach(updatedClassroomStudent);
        updatedClassroomStudent.classCode(UPDATED_CLASS_CODE).studentCode(UPDATED_STUDENT_CODE);

        restClassroomStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClassroomStudent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClassroomStudent))
            )
            .andExpect(status().isOk());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
        ClassroomStudent testClassroomStudent = classroomStudentList.get(classroomStudentList.size() - 1);
        assertThat(testClassroomStudent.getClassCode()).isEqualTo(UPDATED_CLASS_CODE);
        assertThat(testClassroomStudent.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void putNonExistingClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, classroomStudent.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClassroomStudentWithPatch() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();

        // Update the classroomStudent using partial update
        ClassroomStudent partialUpdatedClassroomStudent = new ClassroomStudent();
        partialUpdatedClassroomStudent.setId(classroomStudent.getId());

        restClassroomStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassroomStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassroomStudent))
            )
            .andExpect(status().isOk());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
        ClassroomStudent testClassroomStudent = classroomStudentList.get(classroomStudentList.size() - 1);
        assertThat(testClassroomStudent.getClassCode()).isEqualTo(DEFAULT_CLASS_CODE);
        assertThat(testClassroomStudent.getStudentCode()).isEqualTo(DEFAULT_STUDENT_CODE);
    }

    @Test
    @Transactional
    void fullUpdateClassroomStudentWithPatch() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();

        // Update the classroomStudent using partial update
        ClassroomStudent partialUpdatedClassroomStudent = new ClassroomStudent();
        partialUpdatedClassroomStudent.setId(classroomStudent.getId());

        partialUpdatedClassroomStudent.classCode(UPDATED_CLASS_CODE).studentCode(UPDATED_STUDENT_CODE);

        restClassroomStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClassroomStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClassroomStudent))
            )
            .andExpect(status().isOk());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
        ClassroomStudent testClassroomStudent = classroomStudentList.get(classroomStudentList.size() - 1);
        assertThat(testClassroomStudent.getClassCode()).isEqualTo(UPDATED_CLASS_CODE);
        assertThat(testClassroomStudent.getStudentCode()).isEqualTo(UPDATED_STUDENT_CODE);
    }

    @Test
    @Transactional
    void patchNonExistingClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, classroomStudent.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isBadRequest());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClassroomStudent() throws Exception {
        int databaseSizeBeforeUpdate = classroomStudentRepository.findAll().size();
        classroomStudent.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClassroomStudentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(classroomStudent))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ClassroomStudent in the database
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClassroomStudent() throws Exception {
        // Initialize the database
        classroomStudentRepository.saveAndFlush(classroomStudent);

        int databaseSizeBeforeDelete = classroomStudentRepository.findAll().size();

        // Delete the classroomStudent
        restClassroomStudentMockMvc
            .perform(delete(ENTITY_API_URL_ID, classroomStudent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ClassroomStudent> classroomStudentList = classroomStudentRepository.findAll();
        assertThat(classroomStudentList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
