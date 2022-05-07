package com.toeic.online.service;

import com.toeic.online.service.dto.QuestionDTO;
import com.toeic.online.service.dto.SearchQuestionDTO;
import com.toeic.online.service.dto.ServiceResult;
import com.toeic.online.service.dto.StudentDTO;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {
    Map<String, Object> search(SearchQuestionDTO searchQuestionDTO, Integer page, Integer pageSize);

    List<QuestionDTO> export(SearchQuestionDTO searchQuestionDTO);

    QuestionDTO findById(Long id);

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importQuestion(MultipartFile fileUploads, Long typeImport) throws Exception;

    byte[] exportExcelStudentErrors(List<QuestionDTO> listDataErrors) throws Exception;
}
