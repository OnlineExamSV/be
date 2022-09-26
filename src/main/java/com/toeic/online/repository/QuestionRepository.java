package com.toeic.online.repository;

import com.toeic.online.domain.Question;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM thi_online.question q WHERE q.id in (?1)", nativeQuery = true)
    List<Question> findByListId(long[] ids);
}
