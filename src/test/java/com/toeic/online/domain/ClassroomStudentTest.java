package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassroomStudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClassroomStudent.class);
        ClassroomStudent classroomStudent1 = new ClassroomStudent();
        classroomStudent1.setId(1L);
        ClassroomStudent classroomStudent2 = new ClassroomStudent();
        classroomStudent2.setId(classroomStudent1.getId());
        assertThat(classroomStudent1).isEqualTo(classroomStudent2);
        classroomStudent2.setId(2L);
        assertThat(classroomStudent1).isNotEqualTo(classroomStudent2);
        classroomStudent1.setId(null);
        assertThat(classroomStudent1).isNotEqualTo(classroomStudent2);
    }
}
