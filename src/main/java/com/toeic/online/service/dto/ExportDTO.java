package com.toeic.online.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class ExportDTO implements Serializable {
    private Integer recordNo;

    private String messageStr;

    private List<String> fieldErr = new ArrayList<>();

    private List<String> messageErr = new ArrayList<>();
}
