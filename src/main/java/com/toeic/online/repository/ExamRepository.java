package com.toeic.online.repository;

import com.toeic.online.domain.Exam;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Exam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query(
        value = "select ex.* \n" +
        "from exam ex join subject s on ex.subject_code = s.code \n" +
        "join classroom c on s.class_code = c.code \n" +
        "join teacher t on t.code = c.teacher_code \n" +
        "where t.code = ?1",
        nativeQuery = true
    )
    List<Exam> getListExamByTeacherCode(String teacherCode);
}
