package com.toeic.online.service;

import com.toeic.online.service.dto.SearchSubjectDTO;
import com.toeic.online.service.dto.ServiceResult;
import com.toeic.online.service.dto.SubjectDTO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface SubjectService {
    List<SubjectDTO> exportData(SearchSubjectDTO searchSubjectDTO);

    ByteArrayInputStream getSampleFile() throws IOException;

    Map<String, Object> search(SearchSubjectDTO searchSubjectDTO, Integer page, Integer pageSize);

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importSubject(MultipartFile fileUploads, Long typeImport) throws Exception;

    byte[] exportExcelSubjectErrors(List<SubjectDTO> listDataErrors) throws Exception;
}
