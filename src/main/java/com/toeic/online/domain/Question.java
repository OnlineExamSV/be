package com.toeic.online.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A Question.
 */
@Entity
@Table(name = "question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "question_text")
    private String questionText;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "level")
    private Integer level;

    @Column(name = "point")
    private Float point;

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

    public Question id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionType() {
        return this.questionType;
    }

    public Question questionType(String questionType) {
        this.setQuestionType(questionType);
        return this;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public Question questionText(String questionText) {
        this.setQuestionText(questionText);
        return this;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getSubjectCode() {
        return this.subjectCode;
    }

    public Question subjectCode(String subjectCode) {
        this.setSubjectCode(subjectCode);
        return this;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public Integer getLevel() {
        return this.level;
    }

    public Question level(Integer level) {
        this.setLevel(level);
        return this;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Float getPoint() {
        return this.point;
    }

    public Question point(Float point) {
        this.setPoint(point);
        return this;
    }

    public void setPoint(Float point) {
        this.point = point;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public Question status(Boolean status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Instant getCreateDate() {
        return this.createDate;
    }

    public Question createDate(Instant createDate) {
        this.setCreateDate(createDate);
        return this;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public String getCreateName() {
        return this.createName;
    }

    public Question createName(String createName) {
        this.setCreateName(createName);
        return this;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public Instant getUpdateDate() {
        return this.updateDate;
    }

    public Question updateDate(Instant updateDate) {
        this.setUpdateDate(updateDate);
        return this;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateName() {
        return this.updateName;
    }

    public Question updateName(String updateName) {
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
        if (!(o instanceof Question)) {
            return false;
        }
        return id != null && id.equals(((Question) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Question{" +
            "id=" + getId() +
            ", questionType='" + getQuestionType() + "'" +
            ", questionText='" + getQuestionText() + "'" +
            ", subjectCode='" + getSubjectCode() + "'" +
            ", level=" + getLevel() +
            ", point=" + getPoint() +
            ", status='" + getStatus() + "'" +
            ", createDate='" + getCreateDate() + "'" +
            ", createName='" + getCreateName() + "'" +
            ", updateDate='" + getUpdateDate() + "'" +
            ", updateName='" + getUpdateName() + "'" +
            "}";
    }
}
