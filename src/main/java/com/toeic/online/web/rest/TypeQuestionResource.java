package com.toeic.online.web.rest;

import com.toeic.online.domain.TypeQuestion;
import com.toeic.online.repository.TypeQuestionRepository;
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
 * REST controller for managing {@link com.toeic.online.domain.TypeQuestion}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TypeQuestionResource {

    private final Logger log = LoggerFactory.getLogger(TypeQuestionResource.class);

    private static final String ENTITY_NAME = "typeQuestion";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeQuestionRepository typeQuestionRepository;

    public TypeQuestionResource(TypeQuestionRepository typeQuestionRepository) {
        this.typeQuestionRepository = typeQuestionRepository;
    }

    /**
     * {@code POST  /type-questions} : Create a new typeQuestion.
     *
     * @param typeQuestion the typeQuestion to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeQuestion, or with status {@code 400 (Bad Request)} if the typeQuestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/type-questions")
    public ResponseEntity<TypeQuestion> createTypeQuestion(@RequestBody TypeQuestion typeQuestion) throws URISyntaxException {
        log.debug("REST request to save TypeQuestion : {}", typeQuestion);
        if (typeQuestion.getId() != null) {
            throw new BadRequestAlertException("A new typeQuestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeQuestion result = typeQuestionRepository.save(typeQuestion);
        return ResponseEntity
            .created(new URI("/api/type-questions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /type-questions/:id} : Updates an existing typeQuestion.
     *
     * @param id the id of the typeQuestion to save.
     * @param typeQuestion the typeQuestion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeQuestion,
     * or with status {@code 400 (Bad Request)} if the typeQuestion is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeQuestion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/type-questions/{id}")
    public ResponseEntity<TypeQuestion> updateTypeQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TypeQuestion typeQuestion
    ) throws URISyntaxException {
        log.debug("REST request to update TypeQuestion : {}, {}", id, typeQuestion);
        if (typeQuestion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeQuestion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TypeQuestion result = typeQuestionRepository.save(typeQuestion);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeQuestion.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /type-questions/:id} : Partial updates given fields of an existing typeQuestion, field will ignore if it is null
     *
     * @param id the id of the typeQuestion to save.
     * @param typeQuestion the typeQuestion to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeQuestion,
     * or with status {@code 400 (Bad Request)} if the typeQuestion is not valid,
     * or with status {@code 404 (Not Found)} if the typeQuestion is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeQuestion couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/type-questions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TypeQuestion> partialUpdateTypeQuestion(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TypeQuestion typeQuestion
    ) throws URISyntaxException {
        log.debug("REST request to partial update TypeQuestion partially : {}, {}", id, typeQuestion);
        if (typeQuestion.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeQuestion.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeQuestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeQuestion> result = typeQuestionRepository
            .findById(typeQuestion.getId())
            .map(existingTypeQuestion -> {
                if (typeQuestion.getCode() != null) {
                    existingTypeQuestion.setCode(typeQuestion.getCode());
                }
                if (typeQuestion.getName() != null) {
                    existingTypeQuestion.setName(typeQuestion.getName());
                }

                return existingTypeQuestion;
            })
            .map(typeQuestionRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeQuestion.getId().toString())
        );
    }

    /**
     * {@code GET  /type-questions} : get all the typeQuestions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeQuestions in body.
     */
    @GetMapping("/type-questions")
    public List<TypeQuestion> getAllTypeQuestions() {
        log.debug("REST request to get all TypeQuestions");
        return typeQuestionRepository.findAll();
    }

    /**
     * {@code GET  /type-questions/:id} : get the "id" typeQuestion.
     *
     * @param id the id of the typeQuestion to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeQuestion, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/type-questions/{id}")
    public ResponseEntity<TypeQuestion> getTypeQuestion(@PathVariable Long id) {
        log.debug("REST request to get TypeQuestion : {}", id);
        Optional<TypeQuestion> typeQuestion = typeQuestionRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(typeQuestion);
    }

    /**
     * {@code DELETE  /type-questions/:id} : delete the "id" typeQuestion.
     *
     * @param id the id of the typeQuestion to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/type-questions/{id}")
    public ResponseEntity<Void> deleteTypeQuestion(@PathVariable Long id) {
        log.debug("REST request to delete TypeQuestion : {}", id);
        typeQuestionRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
