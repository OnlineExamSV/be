package com.toeic.online.repository;

import com.toeic.online.domain.Classroom;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Classroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    @Query(value = "SELECT * FROM classroom c WHERE c.code = ?1", nativeQuery = true)
    Classroom findByCode(String code);

    @Query(value = "SELECT * FROM classroom c WHERE UPPER(code) = UPPER(?1) ", nativeQuery = true)
    List<Classroom> findClassCode(String code);
}
