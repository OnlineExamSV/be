package com.toeic.online.repository;

import com.toeic.online.service.dto.ClassroomDTO;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepositoryCustom {
    List<ClassroomDTO> exportData(ClassroomSearchDTO classroomSearchDTO);

    List<ClassroomDTO> search(ClassroomSearchDTO classroomSearchDTO, Integer page, Integer pageSize);
}
