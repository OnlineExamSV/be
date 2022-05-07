package com.toeic.online.service;

import com.toeic.online.service.dto.ClassroomSearchDTO;
import com.toeic.online.service.dto.ExamDTO;
import com.toeic.online.service.dto.StudentDTO;
import java.util.List;
import java.util.Map;

public interface ExamService {
    Map<String, Object> search(ClassroomSearchDTO subjectCode, Integer page, Integer pageSize);

    List<ExamDTO> export(ClassroomSearchDTO subjectCode);

    List<ExamDTO> getListExamByStudentCode(String studentCode);

    ExamDTO dataExamStudent(Long id);

    List<StudentDTO> getPointExamStudent(Long examId);
}
