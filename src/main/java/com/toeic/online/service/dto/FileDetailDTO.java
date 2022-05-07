package com.toeic.online.service.dto;

import lombok.Data;

@Data
public class FileDetailDTO {
    private String path;

    private String name;

    private Long size;

    private String sizeStr;
}
