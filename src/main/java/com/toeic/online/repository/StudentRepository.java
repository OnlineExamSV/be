package com.toeic.online.repository;

import com.toeic.online.domain.Student;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Student entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query(
        value = "select * from student s where s.code not in (select cs.student_code from classroom_student cs where cs.class_code = ?1)",
        nativeQuery = true
    )
    List<Student> getListStudentNotInClassroomStudent(String classCode);
}
