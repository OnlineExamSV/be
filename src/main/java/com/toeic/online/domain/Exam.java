package com.toeic.online.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A Exam.
 */
@Entity
@Table(name = "exam")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "begin_exam")
    private Instant beginExam;

    @Column(name = "duration_exam")
    private Integer durationExam;

    @Column(name = "finish_exam")
    private Instant finishExam;

    @Column(name = "question_data")
    private String questionData;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "title")
    private String title;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "create_name")
    private String createName;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "update_name")
    private String updateName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Exam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getBeginExam() {
        return this.beginExam;
    }

    public Exam beginExam(Instant beginExam) {
        this.setBeginExam(beginExam);
        return this;
    }

    public void setBeginExam(Instant beginExam) {
        this.beginExam = beginExam;
    }

    public Integer getDurationExam() {
        return this.durationExam;
    }

    public Exam durationExam(Integer durationExam) {
        this.setDurationExam(durationExam);
        return this;
    }

    public void setDurationExam(Integer durationExam) {
        this.durationExam = durationExam;
    }

    public Instant getFinishExam() {
        return this.finishExam;
    }

    public Exam finishExam(Instant finishExam) {
        this.setFinishExam(finishExam);
        return this;
    }

    public void setFinishExam(Instant finishExam) {
        this.finishExam = finishExam;
    }

    public String getQuestionData() {
        return this.questionData;
    }

    public Exam questionData(String questionData) {
        this.setQuestionData(questionData);
        return this;
    }

    public void setQuestionData(String questionData) {
        this.questionData = questionData;
    }

    public String getSubjectCode() {
        return this.subjectCode;
    }

    public Exam subjectCode(String subjectCode) {
        this.setSubjectCode(subjectCode);
        return this;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getTitle() {
        return this.title;
    }

    public Exam title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Exam status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public Exam createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public String getCreateName() {
        return this.createName;
    }

    public Exam createName(String createName) {
        this.setCreateName(createName);
        return this;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public Exam updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateName() {
        return this.updateName;
    }

    public Exam updateName(String updateName) {
        this.setUpdateName(updateName);
        return this;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exam)) {
            return false;
        }
        return id != null && id.equals(((Exam) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Exam{" +
            "id=" + getId() +
            ", beginExam='" + getBeginExam() + "'" +
            ", durationExam=" + getDurationExam() +
            ", finishExam='" + getFinishExam() + "'" +
            ", questionData='" + getQuestionData() + "'" +
            ", subjectCode='" + getSubjectCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", createName='" + getCreateName() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", updateName='" + getUpdateName() + "'" +
            "}";
    }
}
