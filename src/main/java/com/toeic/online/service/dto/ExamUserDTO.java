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
public class ExamUserDTO {

    private Long id;
    private String studentCode;
    private Long examId;
    private Float totalPoint;
    private String answerSheet;
    private Instant timeStart;
    private Instant timeFinish;
    private Integer timeRemaining;
}
