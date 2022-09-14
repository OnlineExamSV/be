package com.toeic.online.domain;

import com.toeic.online.service.dto.ClassroomStudentDTO;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A ClassroomStudent.
 */
@Entity
@Table(name = "classroom_student")
public class ClassroomStudent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "class_code")
    private String classCode;

    @Column(name = "student_code")
    private String studentCode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClassroomStudent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassCode() {
        return this.classCode;
    }

    public ClassroomStudent classCode(String classCode) {
        this.setClassCode(classCode);
        return this;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getStudentCode() {
        return this.studentCode;
    }

    public ClassroomStudent studentCode(String studentCode) {
        this.setStudentCode(studentCode);
        return this;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassroomStudent)) {
            return false;
        }
        return id != null && id.equals(((ClassroomStudent) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassroomStudent{" +
            "id=" + getId() +
            ", classCode='" + getClassCode() + "'" +
            ", studentCode='" + getStudentCode() + "'" +
            "}";
    }

    public ClassroomStudent() {}

    public ClassroomStudent(ClassroomStudentDTO dto) {
        this.id = dto.getId();
        this.studentCode = dto.getStudentCode();
        this.classCode = dto.getClassCode();
    }
}
