package com.toeic.online.repository;

import com.toeic.online.domain.ClassroomStudent;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ClassroomStudent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassroomStudentRepository extends JpaRepository<ClassroomStudent, Long> {
    @Query(value = "SELECT * FROM classroom_student cs WHERE cs.class_code = ?1", nativeQuery = true)
    List<ClassroomStudent> getListClassroomStudentByClassCode(String classCode);
}
