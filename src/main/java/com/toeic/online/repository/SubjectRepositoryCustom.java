package com.toeic.online.repository;

import com.toeic.online.service.dto.SearchSubjectDTO;
import com.toeic.online.service.dto.SubjectDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepositoryCustom {
    List<SubjectDTO> exportData(SearchSubjectDTO searchSubjectDTO);

    List<SubjectDTO> search(SearchSubjectDTO searchSubjectDTO, Integer page, Integer pageSize);
}
