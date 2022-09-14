package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.TypeExam;
import com.toeic.online.repository.TypeExamRepository;
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
 * Integration tests for the {@link TypeExamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeExamResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-exams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeExamRepository typeExamRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeExamMockMvc;

    private TypeExam typeExam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeExam createEntity(EntityManager em) {
        TypeExam typeExam = new TypeExam().code(DEFAULT_CODE).name(DEFAULT_NAME);
        return typeExam;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeExam createUpdatedEntity(EntityManager em) {
        TypeExam typeExam = new TypeExam().code(UPDATED_CODE).name(UPDATED_NAME);
        return typeExam;
    }

    @BeforeEach
    public void initTest() {
        typeExam = createEntity(em);
    }

    @Test
    @Transactional
    void createTypeExam() throws Exception {
        int databaseSizeBeforeCreate = typeExamRepository.findAll().size();
        // Create the TypeExam
        restTypeExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeExam)))
            .andExpect(status().isCreated());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeCreate + 1);
        TypeExam testTypeExam = typeExamList.get(typeExamList.size() - 1);
        assertThat(testTypeExam.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTypeExam.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createTypeExamWithExistingId() throws Exception {
        // Create the TypeExam with an existing ID
        typeExam.setId(1L);

        int databaseSizeBeforeCreate = typeExamRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeExamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeExam)))
            .andExpect(status().isBadRequest());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTypeExams() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        // Get all the typeExamList
        restTypeExamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeExam.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getTypeExam() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        // Get the typeExam
        restTypeExamMockMvc
            .perform(get(ENTITY_API_URL_ID, typeExam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeExam.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTypeExam() throws Exception {
        // Get the typeExam
        restTypeExamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypeExam() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();

        // Update the typeExam
        TypeExam updatedTypeExam = typeExamRepository.findById(typeExam.getId()).get();
        // Disconnect from session so that the updates on updatedTypeExam are not directly saved in db
        em.detach(updatedTypeExam);
        updatedTypeExam.code(UPDATED_CODE).name(UPDATED_NAME);

        restTypeExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTypeExam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTypeExam))
            )
            .andExpect(status().isOk());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
        TypeExam testTypeExam = typeExamList.get(typeExamList.size() - 1);
        assertThat(testTypeExam.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTypeExam.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeExam.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeExam))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeExam))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeExam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeExamWithPatch() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();

        // Update the typeExam using partial update
        TypeExam partialUpdatedTypeExam = new TypeExam();
        partialUpdatedTypeExam.setId(typeExam.getId());

        partialUpdatedTypeExam.name(UPDATED_NAME);

        restTypeExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeExam))
            )
            .andExpect(status().isOk());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
        TypeExam testTypeExam = typeExamList.get(typeExamList.size() - 1);
        assertThat(testTypeExam.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTypeExam.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTypeExamWithPatch() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();

        // Update the typeExam using partial update
        TypeExam partialUpdatedTypeExam = new TypeExam();
        partialUpdatedTypeExam.setId(typeExam.getId());

        partialUpdatedTypeExam.code(UPDATED_CODE).name(UPDATED_NAME);

        restTypeExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeExam))
            )
            .andExpect(status().isOk());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
        TypeExam testTypeExam = typeExamList.get(typeExamList.size() - 1);
        assertThat(testTypeExam.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTypeExam.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeExam.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeExam))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeExam))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeExam() throws Exception {
        int databaseSizeBeforeUpdate = typeExamRepository.findAll().size();
        typeExam.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeExamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(typeExam)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeExam in the database
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeExam() throws Exception {
        // Initialize the database
        typeExamRepository.saveAndFlush(typeExam);

        int databaseSizeBeforeDelete = typeExamRepository.findAll().size();

        // Delete the typeExam
        restTypeExamMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeExam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypeExam> typeExamList = typeExamRepository.findAll();
        assertThat(typeExamList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
