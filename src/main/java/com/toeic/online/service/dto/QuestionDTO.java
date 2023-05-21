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
public class QuestionDTO extends ExportDTO {

    private Long id;
    private String questionType;
    private String questionTypeName;
    private String questionText;
    private String subjectCode;
    private String subjectName;
    private Integer level;
    private Float point;
    private Boolean status;
    private Instant createDate;
    private String createName;
    private Instant updateDate;
    private String updateName;
    private List<ChoiceDTO> lstChoice;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private String choiceTrue;
}
