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

    @GetMapping("/history-contacts")
    public List<HistoryContact> getAllHistoryContacts() {
        log.debug("REST request to get all HistoryContacts");
        return historyContactRepository.findAll();
    }

    @GetMapping("/history-contacts/{id}")
    public ResponseEntity<HistoryContact> getHistoryContact(@PathVariable Long id) {
        log.debug("REST request to get HistoryContact : {}", id);
        Optional<HistoryContact> historyContact = historyContactRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(historyContact);
    }

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
