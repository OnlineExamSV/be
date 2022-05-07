package com.toeic.online.repository;

import com.toeic.online.domain.ExamUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ExamUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamUserRepository extends JpaRepository<ExamUser, Long> {}
