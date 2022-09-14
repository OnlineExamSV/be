package com.toeic.online.service;

import com.toeic.online.domain.ClassroomStudent;
import com.toeic.online.service.dto.ClassroomDTO;
import com.toeic.online.service.dto.ClassroomStudentDTO;
import com.toeic.online.service.dto.ServiceResult;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ClassroomStudentService {
    Map<String, Object> search(String classCode, String studentCode, Integer page, Integer pageSize);

    List<ClassroomStudentDTO> exportData(String classCode, String studentCode);

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importClassroomStudent(MultipartFile fileUploads, String classCode, Long typeImport) throws Exception;

    byte[] exportExcelClassroomErrors(List<ClassroomStudentDTO> listDataErrors) throws Exception;
}
