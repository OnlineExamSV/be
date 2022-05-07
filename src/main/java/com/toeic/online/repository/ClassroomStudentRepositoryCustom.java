package com.toeic.online.repository;

import com.toeic.online.service.dto.ClassroomStudentDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomStudentRepositoryCustom {

    List<ClassroomStudentDTO> search(String classCode, String studentCode, Integer page, Integer pageSize);

    List<ClassroomStudentDTO> exportData(String classCode, String studentCode);
}
