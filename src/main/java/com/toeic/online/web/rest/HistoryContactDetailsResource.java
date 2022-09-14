package com.toeic.online.web.rest;

import com.toeic.online.domain.HistoryContactDetails;
import com.toeic.online.repository.HistoryContactDetailsRepository;
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
 * REST controller for managing {@link com.toeic.online.domain.HistoryContactDetails}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HistoryContactDetailsResource {

    private final Logger log = LoggerFactory.getLogger(HistoryContactDetailsResource.class);

    private static final String ENTITY_NAME = "historyContactDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HistoryContactDetailsRepository historyContactDetailsRepository;

    public HistoryContactDetailsResource(HistoryContactDetailsRepository historyContactDetailsRepository) {
        this.historyContactDetailsRepository = historyContactDetailsRepository;
    }

    /**
     * {@code POST  /history-contact-details} : Create a new historyContactDetails.
     *
     * @param historyContactDetails the historyContactDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new historyContactDetails, or with status {@code 400 (Bad Request)} if the historyContactDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/history-contact-details")
    public ResponseEntity<HistoryContactDetails> createHistoryContactDetails(@RequestBody HistoryContactDetails historyContactDetails)
        throws URISyntaxException {
        log.debug("REST request to save HistoryContactDetails : {}", historyContactDetails);
        if (historyContactDetails.getId() != null) {
            throw new BadRequestAlertException("A new historyContactDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        HistoryContactDetails result = historyContactDetailsRepository.save(historyContactDetails);
        return ResponseEntity
            .created(new URI("/api/history-contact-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /history-contact-details/:id} : Updates an existing historyContactDetails.
     *
     * @param id the id of the historyContactDetails to save.
     * @param historyContactDetails the historyContactDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historyContactDetails,
     * or with status {@code 400 (Bad Request)} if the historyContactDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the historyContactDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/history-contact-details/{id}")
    public ResponseEntity<HistoryContactDetails> updateHistoryContactDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistoryContactDetails historyContactDetails
    ) throws URISyntaxException {
        log.debug("REST request to update HistoryContactDetails : {}, {}", id, historyContactDetails);
        if (historyContactDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historyContactDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyContactDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        HistoryContactDetails result = historyContactDetailsRepository.save(historyContactDetails);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historyContactDetails.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /history-contact-details/:id} : Partial updates given fields of an existing historyContactDetails, field will ignore if it is null
     *
     * @param id the id of the historyContactDetails to save.
     * @param historyContactDetails the historyContactDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historyContactDetails,
     * or with status {@code 400 (Bad Request)} if the historyContactDetails is not valid,
     * or with status {@code 404 (Not Found)} if the historyContactDetails is not found,
     * or with status {@code 500 (Internal Server Error)} if the historyContactDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/history-contact-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<HistoryContactDetails> partialUpdateHistoryContactDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody HistoryContactDetails historyContactDetails
    ) throws URISyntaxException {
        log.debug("REST request to partial update HistoryContactDetails partially : {}, {}", id, historyContactDetails);
        if (historyContactDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historyContactDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historyContactDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HistoryContactDetails> result = historyContactDetailsRepository
            .findById(historyContactDetails.getId())
            .map(existingHistoryContactDetails -> {
                if (historyContactDetails.getHistoryContact() != null) {
                    existingHistoryContactDetails.setHistoryContact(historyContactDetails.getHistoryContact());
                }
                if (historyContactDetails.getIsOpen() != null) {
                    existingHistoryContactDetails.setIsOpen(historyContactDetails.getIsOpen());
                }
                if (historyContactDetails.getStatus() != null) {
                    existingHistoryContactDetails.setStatus(historyContactDetails.getStatus());
                }

                return existingHistoryContactDetails;
            })
            .map(historyContactDetailsRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historyContactDetails.getId().toString())
        );
    }

    /**
     * {@code GET  /history-contact-details} : get all the historyContactDetails.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of historyContactDetails in body.
     */
    @GetMapping("/history-contact-details")
    public List<HistoryContactDetails> getAllHistoryContactDetails() {
        log.debug("REST request to get all HistoryContactDetails");
        return historyContactDetailsRepository.findAll();
    }

    /**
     * {@code GET  /history-contact-details/:id} : get the "id" historyContactDetails.
     *
     * @param id the id of the historyContactDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historyContactDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/history-contact-details/{id}")
    public ResponseEntity<HistoryContactDetails> getHistoryContactDetails(@PathVariable Long id) {
        log.debug("REST request to get HistoryContactDetails : {}", id);
        Optional<HistoryContactDetails> historyContactDetails = historyContactDetailsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyContactDetails);
    }

    /**
     * {@code DELETE  /history-contact-details/:id} : delete the "id" historyContactDetails.
     *
     * @param id the id of the historyContactDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/history-contact-details/{id}")
    public ResponseEntity<Void> deleteHistoryContactDetails(@PathVariable Long id) {
        log.debug("REST request to delete HistoryContactDetails : {}", id);
        historyContactDetailsRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
