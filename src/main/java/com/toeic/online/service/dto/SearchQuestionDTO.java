package com.toeic.online.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchQuestionDTO {

    private String name;
    private Integer status;
    private String subjectCode;
}
