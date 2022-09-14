package com.toeic.online.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamStudentDTO {

    private Long id;
    private String studentCode;
    private Long examId;
    private Float totalPoint;
    private String answerSheet;
    private Instant timeStart;
    private Instant timeFinish;
    private Integer timeRemaining;
    private List<QuestionDTO> lstQuestion;
}
