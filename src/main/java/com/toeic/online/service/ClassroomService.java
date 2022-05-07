package com.toeic.online.service;

import com.toeic.online.service.dto.ClassroomDTO;
import com.toeic.online.service.dto.ClassroomSearchDTO;
import com.toeic.online.service.dto.ServiceResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface ClassroomService {
    List<ClassroomDTO> exportData(ClassroomSearchDTO classroomSearchDTO);

    ByteArrayInputStream getSampleFile() throws IOException;

    Map<String, Object> search(ClassroomSearchDTO classroomSearchDTO, Integer page, Integer pageSize);

    List<ClassroomDTO> importClassRoom(MultipartFile file, String fileInputPath, Integer isAddNew, String year) throws Exception;

    byte[] exportFileTemplate() throws Exception;

    ServiceResult<?> importClassroom(MultipartFile fileUploads, Long typeImport) throws Exception;

    byte[] exportExcelClassroomErrors(List<ClassroomDTO> listDataErrors) throws Exception;
}
