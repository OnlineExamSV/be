package com.toeic.online.repository;

import com.toeic.online.domain.Subject;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Subject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query(value = "SELECT * FROM subject s WHERE s.code = ?1", nativeQuery = true)
    Subject findByCode(String code);

    @Query(value = "SELECT * FROM subject s INNER JOIN classroom c ON s.class_code = c.code WHERE c.code = ?1", nativeQuery = true)
    List<Subject> findByClassCode(String classCode);
}
