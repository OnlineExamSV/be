package com.toeic.online.repository;

import com.toeic.online.domain.Exam;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import com.toeic.online.service.dto.ExamDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepositoryCustom {
    List<ExamDTO> search(ClassroomSearchDTO subjectCode, Integer page, Integer pageSize);

    List<ExamDTO> export(ClassroomSearchDTO subjectCode);

    List<ExamDTO> getListExamByStudentCode(String studentCode);
}
