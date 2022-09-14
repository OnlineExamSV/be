package com.toeic.online.service;

import com.toeic.online.service.dto.SearchTeacherDTO;
import com.toeic.online.service.dto.ServiceResult;
import com.toeic.online.service.dto.TeacherDTO;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface TeacherService {
    Map<String, Object> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize);

    List<TeacherDTO> exportData(SearchTeacherDTO searchTeacherDTO);

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importTeacher(MultipartFile fileUploads, Long typeImport) throws Exception;

    byte[] exportExcelTeacherErrors(List<TeacherDTO> listDataErrors) throws Exception;
}
