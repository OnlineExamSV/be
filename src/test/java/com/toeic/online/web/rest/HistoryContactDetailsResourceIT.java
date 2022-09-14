package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.HistoryContactDetails;
import com.toeic.online.repository.HistoryContactDetailsRepository;
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
 * Integration tests for the {@link HistoryContactDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HistoryContactDetailsResourceIT {

    private static final String DEFAULT_HISTORY_CONTACT = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_CONTACT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_OPEN = false;
    private static final Boolean UPDATED_IS_OPEN = true;

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/history-contact-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistoryContactDetailsRepository historyContactDetailsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoryContactDetailsMockMvc;

    private HistoryContactDetails historyContactDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryContactDetails createEntity(EntityManager em) {
        HistoryContactDetails historyContactDetails = new HistoryContactDetails()
            .historyContact(DEFAULT_HISTORY_CONTACT)
            .isOpen(DEFAULT_IS_OPEN)
            .status(DEFAULT_STATUS);
        return historyContactDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryContactDetails createUpdatedEntity(EntityManager em) {
        HistoryContactDetails historyContactDetails = new HistoryContactDetails()
            .historyContact(UPDATED_HISTORY_CONTACT)
            .isOpen(UPDATED_IS_OPEN)
            .status(UPDATED_STATUS);
        return historyContactDetails;
    }

    @BeforeEach
    public void initTest() {
        historyContactDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createHistoryContactDetails() throws Exception {
        int databaseSizeBeforeCreate = historyContactDetailsRepository.findAll().size();
        // Create the HistoryContactDetails
        restHistoryContactDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isCreated());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        HistoryContactDetails testHistoryContactDetails = historyContactDetailsList.get(historyContactDetailsList.size() - 1);
        assertThat(testHistoryContactDetails.getHistoryContact()).isEqualTo(DEFAULT_HISTORY_CONTACT);
        assertThat(testHistoryContactDetails.getIsOpen()).isEqualTo(DEFAULT_IS_OPEN);
        assertThat(testHistoryContactDetails.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void createHistoryContactDetailsWithExistingId() throws Exception {
        // Create the HistoryContactDetails with an existing ID
        historyContactDetails.setId(1L);

        int databaseSizeBeforeCreate = historyContactDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistoryContactDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHistoryContactDetails() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        // Get all the historyContactDetailsList
        restHistoryContactDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyContactDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].historyContact").value(hasItem(DEFAULT_HISTORY_CONTACT)))
            .andExpect(jsonPath("$.[*].isOpen").value(hasItem(DEFAULT_IS_OPEN.booleanValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())));
    }

    @Test
    @Transactional
    void getHistoryContactDetails() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        // Get the historyContactDetails
        restHistoryContactDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, historyContactDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historyContactDetails.getId().intValue()))
            .andExpect(jsonPath("$.historyContact").value(DEFAULT_HISTORY_CONTACT))
            .andExpect(jsonPath("$.isOpen").value(DEFAULT_IS_OPEN.booleanValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingHistoryContactDetails() throws Exception {
        // Get the historyContactDetails
        restHistoryContactDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHistoryContactDetails() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();

        // Update the historyContactDetails
        HistoryContactDetails updatedHistoryContactDetails = historyContactDetailsRepository.findById(historyContactDetails.getId()).get();
        // Disconnect from session so that the updates on updatedHistoryContactDetails are not directly saved in db
        em.detach(updatedHistoryContactDetails);
        updatedHistoryContactDetails.historyContact(UPDATED_HISTORY_CONTACT).isOpen(UPDATED_IS_OPEN).status(UPDATED_STATUS);

        restHistoryContactDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHistoryContactDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHistoryContactDetails))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
        HistoryContactDetails testHistoryContactDetails = historyContactDetailsList.get(historyContactDetailsList.size() - 1);
        assertThat(testHistoryContactDetails.getHistoryContact()).isEqualTo(UPDATED_HISTORY_CONTACT);
        assertThat(testHistoryContactDetails.getIsOpen()).isEqualTo(UPDATED_IS_OPEN);
        assertThat(testHistoryContactDetails.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void putNonExistingHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historyContactDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistoryContactDetailsWithPatch() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();

        // Update the historyContactDetails using partial update
        HistoryContactDetails partialUpdatedHistoryContactDetails = new HistoryContactDetails();
        partialUpdatedHistoryContactDetails.setId(historyContactDetails.getId());

        partialUpdatedHistoryContactDetails.isOpen(UPDATED_IS_OPEN);

        restHistoryContactDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryContactDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryContactDetails))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
        HistoryContactDetails testHistoryContactDetails = historyContactDetailsList.get(historyContactDetailsList.size() - 1);
        assertThat(testHistoryContactDetails.getHistoryContact()).isEqualTo(DEFAULT_HISTORY_CONTACT);
        assertThat(testHistoryContactDetails.getIsOpen()).isEqualTo(UPDATED_IS_OPEN);
        assertThat(testHistoryContactDetails.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    void fullUpdateHistoryContactDetailsWithPatch() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();

        // Update the historyContactDetails using partial update
        HistoryContactDetails partialUpdatedHistoryContactDetails = new HistoryContactDetails();
        partialUpdatedHistoryContactDetails.setId(historyContactDetails.getId());

        partialUpdatedHistoryContactDetails.historyContact(UPDATED_HISTORY_CONTACT).isOpen(UPDATED_IS_OPEN).status(UPDATED_STATUS);

        restHistoryContactDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryContactDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryContactDetails))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
        HistoryContactDetails testHistoryContactDetails = historyContactDetailsList.get(historyContactDetailsList.size() - 1);
        assertThat(testHistoryContactDetails.getHistoryContact()).isEqualTo(UPDATED_HISTORY_CONTACT);
        assertThat(testHistoryContactDetails.getIsOpen()).isEqualTo(UPDATED_IS_OPEN);
        assertThat(testHistoryContactDetails.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    void patchNonExistingHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historyContactDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistoryContactDetails() throws Exception {
        int databaseSizeBeforeUpdate = historyContactDetailsRepository.findAll().size();
        historyContactDetails.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyContactDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryContactDetails in the database
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistoryContactDetails() throws Exception {
        // Initialize the database
        historyContactDetailsRepository.saveAndFlush(historyContactDetails);

        int databaseSizeBeforeDelete = historyContactDetailsRepository.findAll().size();

        // Delete the historyContactDetails
        restHistoryContactDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, historyContactDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HistoryContactDetails> historyContactDetailsList = historyContactDetailsRepository.findAll();
        assertThat(historyContactDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
