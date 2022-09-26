package com.toeic.online.web.rest;

import com.toeic.online.domain.Choice;
import com.toeic.online.repository.ChoiceRepository;
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
public class ChoiceResource {

    private final Logger log = LoggerFactory.getLogger(ChoiceResource.class);

    private static final String ENTITY_NAME = "choice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChoiceRepository choiceRepository;

    public ChoiceResource(ChoiceRepository choiceRepository) {
        this.choiceRepository = choiceRepository;
    }

    @PostMapping("/choices")
    public ResponseEntity<Choice> createChoice(@RequestBody Choice choice) throws URISyntaxException {
        log.debug("REST request to save Choice : {}", choice);
        if (choice.getId() != null) {
            throw new BadRequestAlertException("A new choice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Choice result = choiceRepository.save(choice);
        return ResponseEntity
            .created(new URI("/api/choices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @GetMapping("/choices")
    public List<Choice> getAllChoices() {
        log.debug("REST request to get all Choices");
        return choiceRepository.findAll();
    }

    @GetMapping("/choices/{id}")
    public ResponseEntity<Choice> getChoice(@PathVariable Long id) {
        log.debug("REST request to get Choice : {}", id);
        Optional<Choice> choice = choiceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(choice);
    }

    @DeleteMapping("/choices/{id}")
    public ResponseEntity<Void> deleteChoice(@PathVariable Long id) {
        log.debug("REST request to delete Choice : {}", id);
        choiceRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
