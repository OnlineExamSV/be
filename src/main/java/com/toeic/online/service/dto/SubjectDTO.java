package com.toeic.online.service.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO extends ExportDTO {

    private Long id;
    private String code;
    private String name;
    private String classCode;
    private String className;
    private Boolean status;
    private String statusStr;
    private Instant createDate;
    private String createName;
    private Instant updateDate;
    private String updateName;
}
