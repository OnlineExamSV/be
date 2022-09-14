package com.toeic.online.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.toeic.online.IntegrationTest;
import com.toeic.online.domain.HistoryContact;
import com.toeic.online.repository.HistoryContactRepository;
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
 * Integration tests for the {@link HistoryContactResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HistoryContactResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_SENDER = "AAAAAAAAAA";
    private static final String UPDATED_SENDER = "BBBBBBBBBB";

    private static final String DEFAULT_TOER = "AAAAAAAAAA";
    private static final String UPDATED_TOER = "BBBBBBBBBB";

    private static final Instant DEFAULT_SEND_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SEND_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/history-contacts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HistoryContactRepository historyContactRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoryContactMockMvc;

    private HistoryContact historyContact;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryContact createEntity(EntityManager em) {
        HistoryContact historyContact = new HistoryContact()
            .code(DEFAULT_CODE)
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .sender(DEFAULT_SENDER)
            .toer(DEFAULT_TOER)
            .sendDate(DEFAULT_SEND_DATE);
        return historyContact;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoryContact createUpdatedEntity(EntityManager em) {
        HistoryContact historyContact = new HistoryContact()
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .sender(UPDATED_SENDER)
            .toer(UPDATED_TOER)
            .sendDate(UPDATED_SEND_DATE);
        return historyContact;
    }

    @BeforeEach
    public void initTest() {
        historyContact = createEntity(em);
    }

    @Test
    @Transactional
    void createHistoryContact() throws Exception {
        int databaseSizeBeforeCreate = historyContactRepository.findAll().size();
        // Create the HistoryContact
        restHistoryContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isCreated());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeCreate + 1);
        HistoryContact testHistoryContact = historyContactList.get(historyContactList.size() - 1);
        assertThat(testHistoryContact.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testHistoryContact.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testHistoryContact.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testHistoryContact.getSender()).isEqualTo(DEFAULT_SENDER);
        assertThat(testHistoryContact.getToer()).isEqualTo(DEFAULT_TOER);
        assertThat(testHistoryContact.getSendDate()).isEqualTo(DEFAULT_SEND_DATE);
    }

    @Test
    @Transactional
    void createHistoryContactWithExistingId() throws Exception {
        // Create the HistoryContact with an existing ID
        historyContact.setId(1L);

        int databaseSizeBeforeCreate = historyContactRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistoryContactMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllHistoryContacts() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        // Get all the historyContactList
        restHistoryContactMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historyContact.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].sender").value(hasItem(DEFAULT_SENDER)))
            .andExpect(jsonPath("$.[*].toer").value(hasItem(DEFAULT_TOER)))
            .andExpect(jsonPath("$.[*].sendDate").value(hasItem(DEFAULT_SEND_DATE.toString())));
    }

    @Test
    @Transactional
    void getHistoryContact() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        // Get the historyContact
        restHistoryContactMockMvc
            .perform(get(ENTITY_API_URL_ID, historyContact.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historyContact.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.sender").value(DEFAULT_SENDER))
            .andExpect(jsonPath("$.toer").value(DEFAULT_TOER))
            .andExpect(jsonPath("$.sendDate").value(DEFAULT_SEND_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingHistoryContact() throws Exception {
        // Get the historyContact
        restHistoryContactMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHistoryContact() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();

        // Update the historyContact
        HistoryContact updatedHistoryContact = historyContactRepository.findById(historyContact.getId()).get();
        // Disconnect from session so that the updates on updatedHistoryContact are not directly saved in db
        em.detach(updatedHistoryContact);
        updatedHistoryContact
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .sender(UPDATED_SENDER)
            .toer(UPDATED_TOER)
            .sendDate(UPDATED_SEND_DATE);

        restHistoryContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHistoryContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHistoryContact))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
        HistoryContact testHistoryContact = historyContactList.get(historyContactList.size() - 1);
        assertThat(testHistoryContact.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testHistoryContact.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testHistoryContact.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testHistoryContact.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testHistoryContact.getToer()).isEqualTo(UPDATED_TOER);
        assertThat(testHistoryContact.getSendDate()).isEqualTo(UPDATED_SEND_DATE);
    }

    @Test
    @Transactional
    void putNonExistingHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historyContact.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(historyContact)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistoryContactWithPatch() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();

        // Update the historyContact using partial update
        HistoryContact partialUpdatedHistoryContact = new HistoryContact();
        partialUpdatedHistoryContact.setId(historyContact.getId());

        partialUpdatedHistoryContact.content(UPDATED_CONTENT).sender(UPDATED_SENDER).toer(UPDATED_TOER);

        restHistoryContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryContact))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
        HistoryContact testHistoryContact = historyContactList.get(historyContactList.size() - 1);
        assertThat(testHistoryContact.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testHistoryContact.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testHistoryContact.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testHistoryContact.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testHistoryContact.getToer()).isEqualTo(UPDATED_TOER);
        assertThat(testHistoryContact.getSendDate()).isEqualTo(DEFAULT_SEND_DATE);
    }

    @Test
    @Transactional
    void fullUpdateHistoryContactWithPatch() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();

        // Update the historyContact using partial update
        HistoryContact partialUpdatedHistoryContact = new HistoryContact();
        partialUpdatedHistoryContact.setId(historyContact.getId());

        partialUpdatedHistoryContact
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .sender(UPDATED_SENDER)
            .toer(UPDATED_TOER)
            .sendDate(UPDATED_SEND_DATE);

        restHistoryContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoryContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHistoryContact))
            )
            .andExpect(status().isOk());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
        HistoryContact testHistoryContact = historyContactList.get(historyContactList.size() - 1);
        assertThat(testHistoryContact.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testHistoryContact.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testHistoryContact.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testHistoryContact.getSender()).isEqualTo(UPDATED_SENDER);
        assertThat(testHistoryContact.getToer()).isEqualTo(UPDATED_TOER);
        assertThat(testHistoryContact.getSendDate()).isEqualTo(UPDATED_SEND_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historyContact.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistoryContact() throws Exception {
        int databaseSizeBeforeUpdate = historyContactRepository.findAll().size();
        historyContact.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoryContactMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(historyContact))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoryContact in the database
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistoryContact() throws Exception {
        // Initialize the database
        historyContactRepository.saveAndFlush(historyContact);

        int databaseSizeBeforeDelete = historyContactRepository.findAll().size();

        // Delete the historyContact
        restHistoryContactMockMvc
            .perform(delete(ENTITY_API_URL_ID, historyContact.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<HistoryContact> historyContactList = historyContactRepository.findAll();
        assertThat(historyContactList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
