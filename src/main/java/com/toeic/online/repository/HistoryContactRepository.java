package com.toeic.online.repository;

import com.toeic.online.domain.HistoryContact;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HistoryContact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistoryContactRepository extends JpaRepository<HistoryContact, Long> {}
