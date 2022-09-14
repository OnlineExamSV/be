package com.toeic.online.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A ExamUser.
 */
@Entity
@Table(name = "exam_user")
public class ExamUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "total_point")
    private Float totalPoint;

    @Column(name = "answer_sheet")
    private String answerSheet;

    @Column(name = "time_start")
    private Instant timeStart;

    @Column(name = "time_finish")
    private Instant timeFinish;

    @Column(name = "time_remaining")
    private Integer timeRemaining;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExamUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return this.studentCode;
    }

    public ExamUser studentCode(String studentCode) {
        this.setStudentCode(studentCode);
        return this;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public Long getExamId() {
        return this.examId;
    }

    public ExamUser examId(Long examId) {
        this.setExamId(examId);
        return this;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Float getTotalPoint() {
        return this.totalPoint;
    }

    public ExamUser totalPoint(Float totalPoint) {
        this.setTotalPoint(totalPoint);
        return this;
    }

    public void setTotalPoint(Float totalPoint) {
        this.totalPoint = totalPoint;
    }

    public String getAnswerSheet() {
        return this.answerSheet;
    }

    public ExamUser answerSheet(String answerSheet) {
        this.setAnswerSheet(answerSheet);
        return this;
    }

    public void setAnswerSheet(String answerSheet) {
        this.answerSheet = answerSheet;
    }

    public Instant getTimeStart() {
        return this.timeStart;
    }

    public ExamUser timeStart(Instant timeStart) {
        this.setTimeStart(timeStart);
        return this;
    }

    public void setTimeStart(Instant timeStart) {
        this.timeStart = timeStart;
    }

    public Instant getTimeFinish() {
        return this.timeFinish;
    }

    public ExamUser timeFinish(Instant timeFinish) {
        this.setTimeFinish(timeFinish);
        return this;
    }

    public void setTimeFinish(Instant timeFinish) {
        this.timeFinish = timeFinish;
    }

    public Integer getTimeRemaining() {
        return this.timeRemaining;
    }

    public ExamUser timeRemaining(Integer timeRemaining) {
        this.setTimeRemaining(timeRemaining);
        return this;
    }

    public void setTimeRemaining(Integer timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExamUser)) {
            return false;
        }
        return id != null && id.equals(((ExamUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExamUser{" +
            "id=" + getId() +
            ", studentCode='" + getStudentCode() + "'" +
            ", examId=" + getExamId() +
            ", totalPoint=" + getTotalPoint() +
            ", answerSheet='" + getAnswerSheet() + "'" +
            ", timeStart='" + getTimeStart() + "'" +
            ", timeFinish='" + getTimeFinish() + "'" +
            ", timeRemaining=" + getTimeRemaining() +
            "}";
    }
}
