package com.toeic.online.repository;

import com.toeic.online.service.dto.SearchTeacherDTO;
import com.toeic.online.service.dto.TeacherDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepositoryCustom {

    List<TeacherDTO> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize);

    List<TeacherDTO> exportData(SearchTeacherDTO searchTeacherDTO);
}
