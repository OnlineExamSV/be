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

    @GetMapping("/history-contact-details")
    public List<HistoryContactDetails> getAllHistoryContactDetails() {
        log.debug("REST request to get all HistoryContactDetails");
        return historyContactDetailsRepository.findAll();
    }

    @GetMapping("/history-contact-details/{id}")
    public ResponseEntity<HistoryContactDetails> getHistoryContactDetails(@PathVariable Long id) {
        log.debug("REST request to get HistoryContactDetails : {}", id);
        Optional<HistoryContactDetails> historyContactDetails = historyContactDetailsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyContactDetails);
    }

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
