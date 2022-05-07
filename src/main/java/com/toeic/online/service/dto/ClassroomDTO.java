package com.toeic.online.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomDTO extends ExportDTO {

    private Long id;
    private String code;
    private String name;
    private String teacherCode;
    private String teacherName;
    private Boolean status;
    private Instant createDate;
    private String createName;
    private Instant updateDate;
    private String updateName;
    private String statusStr;
    private Long totalSuccess;
    private Long totalFail;
    private String errorCodeConfig;
    private String filePath;
    private String filePathError;
    private String studentCode;
    private Boolean currentYear = false;
    private List<Integer> lineSuccess;
    private List<ExcelDynamicDTO> listError;
}
