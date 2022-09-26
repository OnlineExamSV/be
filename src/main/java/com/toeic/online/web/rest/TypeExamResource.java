package com.toeic.online.web.rest;

import com.toeic.online.domain.TypeExam;
import com.toeic.online.repository.TypeExamRepository;
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
public class TypeExamResource {

    private final Logger log = LoggerFactory.getLogger(TypeExamResource.class);

    private static final String ENTITY_NAME = "typeExam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeExamRepository typeExamRepository;

    public TypeExamResource(TypeExamRepository typeExamRepository) {
        this.typeExamRepository = typeExamRepository;
    }

    @PostMapping("/type-exams")
    public ResponseEntity<TypeExam> createTypeExam(@RequestBody TypeExam typeExam) throws URISyntaxException {
        log.debug("REST request to save TypeExam : {}", typeExam);
        if (typeExam.getId() != null) {
            throw new BadRequestAlertException("A new typeExam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeExam result = typeExamRepository.save(typeExam);
        return ResponseEntity
            .created(new URI("/api/type-exams/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/type-exams/{id}")
    public ResponseEntity<TypeExam> updateTypeExam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TypeExam typeExam
    ) throws URISyntaxException {
        log.debug("REST request to update TypeExam : {}, {}", id, typeExam);
        if (typeExam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeExam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeExamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TypeExam result = typeExamRepository.save(typeExam);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeExam.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/type-exams/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TypeExam> partialUpdateTypeExam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TypeExam typeExam
    ) throws URISyntaxException {
        log.debug("REST request to partial update TypeExam partially : {}, {}", id, typeExam);
        if (typeExam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeExam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeExamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeExam> result = typeExamRepository
            .findById(typeExam.getId())
            .map(existingTypeExam -> {
                if (typeExam.getCode() != null) {
                    existingTypeExam.setCode(typeExam.getCode());
                }
                if (typeExam.getName() != null) {
                    existingTypeExam.setName(typeExam.getName());
                }

                return existingTypeExam;
            })
            .map(typeExamRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeExam.getId().toString())
        );
    }

    @GetMapping("/type-exams")
    public List<TypeExam> getAllTypeExams() {
        log.debug("REST request to get all TypeExams");
        return typeExamRepository.findAll();
    }

    @GetMapping("/type-exams/{id}")
    public ResponseEntity<TypeExam> getTypeExam(@PathVariable Long id) {
        log.debug("REST request to get TypeExam : {}", id);
        Optional<TypeExam> typeExam = typeExamRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(typeExam);
    }

    @DeleteMapping("/type-exams/{id}")
    public ResponseEntity<Void> deleteTypeExam(@PathVariable Long id) {
        log.debug("REST request to delete TypeExam : {}", id);
        typeExamRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
