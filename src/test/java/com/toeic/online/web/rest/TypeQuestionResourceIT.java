package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.TypeQuestion;
import com.toeic.online.repository.TypeQuestionRepository;
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
 * Integration tests for the {@link TypeQuestionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeQuestionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-questions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypeQuestionRepository typeQuestionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeQuestionMockMvc;

    private TypeQuestion typeQuestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeQuestion createEntity(EntityManager em) {
        TypeQuestion typeQuestion = new TypeQuestion().code(DEFAULT_CODE).name(DEFAULT_NAME);
        return typeQuestion;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeQuestion createUpdatedEntity(EntityManager em) {
        TypeQuestion typeQuestion = new TypeQuestion().code(UPDATED_CODE).name(UPDATED_NAME);
        return typeQuestion;
    }

    @BeforeEach
    public void initTest() {
        typeQuestion = createEntity(em);
    }

    @Test
    @Transactional
    void createTypeQuestion() throws Exception {
        int databaseSizeBeforeCreate = typeQuestionRepository.findAll().size();
        // Create the TypeQuestion
        restTypeQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeQuestion)))
            .andExpect(status().isCreated());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeCreate + 1);
        TypeQuestion testTypeQuestion = typeQuestionList.get(typeQuestionList.size() - 1);
        assertThat(testTypeQuestion.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testTypeQuestion.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createTypeQuestionWithExistingId() throws Exception {
        // Create the TypeQuestion with an existing ID
        typeQuestion.setId(1L);

        int databaseSizeBeforeCreate = typeQuestionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeQuestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeQuestion)))
            .andExpect(status().isBadRequest());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTypeQuestions() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        // Get all the typeQuestionList
        restTypeQuestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeQuestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getTypeQuestion() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        // Get the typeQuestion
        restTypeQuestionMockMvc
            .perform(get(ENTITY_API_URL_ID, typeQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeQuestion.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingTypeQuestion() throws Exception {
        // Get the typeQuestion
        restTypeQuestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypeQuestion() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();

        // Update the typeQuestion
        TypeQuestion updatedTypeQuestion = typeQuestionRepository.findById(typeQuestion.getId()).get();
        // Disconnect from session so that the updates on updatedTypeQuestion are not directly saved in db
        em.detach(updatedTypeQuestion);
        updatedTypeQuestion.code(UPDATED_CODE).name(UPDATED_NAME);

        restTypeQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTypeQuestion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTypeQuestion))
            )
            .andExpect(status().isOk());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
        TypeQuestion testTypeQuestion = typeQuestionList.get(typeQuestionList.size() - 1);
        assertThat(testTypeQuestion.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTypeQuestion.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeQuestion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeQuestion))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typeQuestion))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(typeQuestion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeQuestionWithPatch() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();

        // Update the typeQuestion using partial update
        TypeQuestion partialUpdatedTypeQuestion = new TypeQuestion();
        partialUpdatedTypeQuestion.setId(typeQuestion.getId());

        partialUpdatedTypeQuestion.code(UPDATED_CODE);

        restTypeQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeQuestion))
            )
            .andExpect(status().isOk());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
        TypeQuestion testTypeQuestion = typeQuestionList.get(typeQuestionList.size() - 1);
        assertThat(testTypeQuestion.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTypeQuestion.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateTypeQuestionWithPatch() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();

        // Update the typeQuestion using partial update
        TypeQuestion partialUpdatedTypeQuestion = new TypeQuestion();
        partialUpdatedTypeQuestion.setId(typeQuestion.getId());

        partialUpdatedTypeQuestion.code(UPDATED_CODE).name(UPDATED_NAME);

        restTypeQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypeQuestion))
            )
            .andExpect(status().isOk());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
        TypeQuestion testTypeQuestion = typeQuestionList.get(typeQuestionList.size() - 1);
        assertThat(testTypeQuestion.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testTypeQuestion.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeQuestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeQuestion))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typeQuestion))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeQuestion() throws Exception {
        int databaseSizeBeforeUpdate = typeQuestionRepository.findAll().size();
        typeQuestion.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeQuestionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(typeQuestion))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeQuestion in the database
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeQuestion() throws Exception {
        // Initialize the database
        typeQuestionRepository.saveAndFlush(typeQuestion);

        int databaseSizeBeforeDelete = typeQuestionRepository.findAll().size();

        // Delete the typeQuestion
        restTypeQuestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeQuestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypeQuestion> typeQuestionList = typeQuestionRepository.findAll();
        assertThat(typeQuestionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
