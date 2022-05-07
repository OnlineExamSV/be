package com.toeic.online.repository;

import com.toeic.online.service.dto.SearchTeacherDTO;
import com.toeic.online.service.dto.StudentDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepositoryCustom {
    List<StudentDTO> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize);

    List<StudentDTO> exportData(SearchTeacherDTO searchTeacherDTO);

    List<StudentDTO> getPointExamStudent(Long examId);
}
