package com.toeic.online.service;

import com.toeic.online.domain.Student;
import com.toeic.online.service.dto.SearchTeacherDTO;
import com.toeic.online.service.dto.ServiceResult;
import com.toeic.online.service.dto.StudentDTO;
import com.toeic.online.service.dto.TeacherDTO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    Map<String, Object> search(SearchTeacherDTO searchTeacherDTO, Integer page, Integer pageSize);

    List<StudentDTO> exportData(SearchTeacherDTO searchTeacherDTO);

    ByteArrayInputStream getSampleFile() throws IOException;

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importStudent(MultipartFile fileUploads, Long typeImport) throws Exception;

    byte[] exportExcelStudentErrors(List<StudentDTO> listDataErrors) throws Exception;
}
