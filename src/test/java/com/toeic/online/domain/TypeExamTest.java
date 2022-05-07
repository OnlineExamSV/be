package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeExamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeExam.class);
        TypeExam typeExam1 = new TypeExam();
        typeExam1.setId(1L);
        TypeExam typeExam2 = new TypeExam();
        typeExam2.setId(typeExam1.getId());
        assertThat(typeExam1).isEqualTo(typeExam2);
        typeExam2.setId(2L);
        assertThat(typeExam1).isNotEqualTo(typeExam2);
        typeExam1.setId(null);
        assertThat(typeExam1).isNotEqualTo(typeExam2);
    }
}
