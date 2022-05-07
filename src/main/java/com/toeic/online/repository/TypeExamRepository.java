package com.toeic.online.repository;

import com.toeic.online.domain.TypeExam;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TypeExam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeExamRepository extends JpaRepository<TypeExam, Long> {}
