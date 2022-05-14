package com.toeic.online.repository;

import com.toeic.online.domain.ExamUser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the ExamUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExamUserRepository extends JpaRepository<ExamUser, Long> {

    @Query(value = "SELECT * FROM exam_user eu WHERE eu.student_code = ?1 AND eu.exam_id = ?2", nativeQuery = true)
    List<ExamUser> getListByStudentCodeAndExamId(String studentCode, Long examId);

}
