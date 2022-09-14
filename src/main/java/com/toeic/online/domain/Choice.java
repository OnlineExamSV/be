package com.toeic.online.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Choice.
 */
@Entity
@Table(name = "choice")
public class Choice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "choice_text")
    private String choiceText;

    @Column(name = "corrected")
    private String corrected;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Choice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return this.questionId;
    }

    public Choice questionId(Long questionId) {
        this.setQuestionId(questionId);
        return this;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getChoiceText() {
        return this.choiceText;
    }

    public Choice choiceText(String choiceText) {
        this.setChoiceText(choiceText);
        return this;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }

    public String getCorrected() {
        return this.corrected;
    }

    public Choice corrected(String corrected) {
        this.setCorrected(corrected);
        return this;
    }

    public void setCorrected(String corrected) {
        this.corrected = corrected;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Choice)) {
            return false;
        }
        return id != null && id.equals(((Choice) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Choice{" +
            "id=" + getId() +
            ", questionId=" + getQuestionId() +
            ", choiceText='" + getChoiceText() + "'" +
            ", corrected='" + getCorrected() + "'" +
            "}";
    }
}
