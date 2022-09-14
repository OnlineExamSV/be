package com.toeic.online.repository;

import com.toeic.online.domain.HistoryContactDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HistoryContactDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistoryContactDetailsRepository extends JpaRepository<HistoryContactDetails, Long> {}
