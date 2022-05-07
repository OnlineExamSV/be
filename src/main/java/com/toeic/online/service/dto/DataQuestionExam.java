package com.toeic.online.service.dto;

import com.toeic.online.domain.Choice;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataQuestionExam {

    private Long id;
    private String questionText;
    private Float point;
    private Integer level;
    private List<Choice> lstChoice;
}
