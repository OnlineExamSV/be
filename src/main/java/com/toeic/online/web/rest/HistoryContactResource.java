package com.toeic.online.web.rest;

import com.toeic.online.domain.HistoryContact;
import com.toeic.online.repository.HistoryContactRepository;
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
 * REST controller for managing {@link com.toeic.online.domain.HistoryContact}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistoryContactResource {

    private final Logger log = LoggerFactory.getLogger(HistoryContactResource.class);

    private static final String ENTITY_NAME = "historyContact";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HistoryContactRepository historyContactRepository;

    public HistoryContactResource(HistoryContactRepository historyContactRepository) {
        this.historyContactRepository = historyContactRepository;
    }

    /**
     * {@code POST  /history-contacts} : Create a new historyContact.
     *
     * @param historyContact the historyContact to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new historyContact, or with status {@code 400 (Bad Request)} if the historyContact has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/history-contacts")
    public ResponseEntity<HistoryContact> createHistoryContact(@RequestBody HistoryContact historyContact) throws URISyntaxException {
        log.debug("REST request to save HistoryContact : {}", historyContact);
        if (historyContact.getId() != null) {
            throw new BadRequestAlertException("A new historyContact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HistoryContact result = historyContactRepository.save(historyContact);
        return ResponseEntity
            .created(new URI("/api/history-contacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /history-contacts/:id} : Updates an existing historyContact.
     *
     * @param id the id of the historyContact to save.
     * @param historyContact the historyContact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historyContact,
     * or with status {@code 400 (Bad Request)} if the historyContact is not valid,
     * or with status {@code 500 (Internal Server Error)} if the historyContact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/history-contacts/{id}")
    public ResponseEntity<HistoryContact> updateHistoryContact(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistoryContact historyContact
    ) throws URISyntaxException {
        log.debug("REST request to update HistoryContact : {}, {}", id, historyContact);
        if (historyContact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historyContact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyContactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HistoryContact result = historyContactRepository.save(historyContact);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historyContact.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /history-contacts/:id} : Partial updates given fields of an existing historyContact, field will ignore if it is null
     *
     * @param id the id of the historyContact to save.
     * @param historyContact the historyContact to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historyContact,
     * or with status {@code 400 (Bad Request)} if the historyContact is not valid,
     * or with status {@code 404 (Not Found)} if the historyContact is not found,
     * or with status {@code 500 (Internal Server Error)} if the historyContact couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/history-contacts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HistoryContact> partialUpdateHistoryContact(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistoryContact historyContact
    ) throws URISyntaxException {
        log.debug("REST request to partial update HistoryContact partially : {}, {}", id, historyContact);
        if (historyContact.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historyContact.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyContactRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HistoryContact> result = historyContactRepository
            .findById(historyContact.getId())
            .map(existingHistoryContact -> {
                if (historyContact.getCode() != null) {
                    existingHistoryContact.setCode(historyContact.getCode());
                }
                if (historyContact.getTitle() != null) {
                    existingHistoryContact.setTitle(historyContact.getTitle());
                }
                if (historyContact.getContent() != null) {
                    existingHistoryContact.setContent(historyContact.getContent());
                }
                if (historyContact.getSender() != null) {
                    existingHistoryContact.setSender(historyContact.getSender());
                }
                if (historyContact.getToer() != null) {
                    existingHistoryContact.setToer(historyContact.getToer());
                }
                if (historyContact.getSendDate() != null) {
                    existingHistoryContact.setSendDate(historyContact.getSendDate());
                }

                return existingHistoryContact;
            })
            .map(historyContactRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historyContact.getId().toString())
        );
    }

    /**
     * {@code GET  /history-contacts} : get all the historyContacts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of historyContacts in body.
     */
    @GetMapping("/history-contacts")
    public List<HistoryContact> getAllHistoryContacts() {
        log.debug("REST request to get all HistoryContacts");
        return historyContactRepository.findAll();
    }

    /**
     * {@code GET  /history-contacts/:id} : get the "id" historyContact.
     *
     * @param id the id of the historyContact to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historyContact, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/history-contacts/{id}")
    public ResponseEntity<HistoryContact> getHistoryContact(@PathVariable Long id) {
        log.debug("REST request to get HistoryContact : {}", id);
        Optional<HistoryContact> historyContact = historyContactRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyContact);
    }

    /**
     * {@code DELETE  /history-contacts/:id} : delete the "id" historyContact.
     *
     * @param id the id of the historyContact to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/history-contacts/{id}")
    public ResponseEntity<Void> deleteHistoryContact(@PathVariable Long id) {
        log.debug("REST request to delete HistoryContact : {}", id);
        historyContactRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
