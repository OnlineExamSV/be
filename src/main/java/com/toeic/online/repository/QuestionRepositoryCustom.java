package com.toeic.online.repository;

import com.toeic.online.service.dto.QuestionDTO;
import com.toeic.online.service.dto.SearchQuestionDTO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepositoryCustom {
    List<QuestionDTO> search(SearchQuestionDTO searchQuestionDTO, Integer page, Integer pageSize);

    List<QuestionDTO> export(SearchQuestionDTO searchQuestionDTO);

    QuestionDTO findByQuestionId(Long questionId);
}
