package com.toeic.online.repository;

import com.toeic.online.domain.Choice;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Choice entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    @Query(value = "SELECT * FROM choice c WHERE c.question_id = ?1", nativeQuery = true)
    List<Choice> getListChoiceByQuestionId(Long id);
}
