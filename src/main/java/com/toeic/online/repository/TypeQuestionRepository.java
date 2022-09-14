package com.toeic.online.repository;

import com.toeic.online.domain.TypeQuestion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TypeQuestion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeQuestionRepository extends JpaRepository<TypeQuestion, Long> {}
