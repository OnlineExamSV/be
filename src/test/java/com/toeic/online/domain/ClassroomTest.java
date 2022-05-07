package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClassroomTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Classroom.class);
        Classroom classroom1 = new Classroom();
        classroom1.setId(1L);
        Classroom classroom2 = new Classroom();
        classroom2.setId(classroom1.getId());
        assertThat(classroom1).isEqualTo(classroom2);
        classroom2.setId(2L);
        assertThat(classroom1).isNotEqualTo(classroom2);
        classroom1.setId(null);
        assertThat(classroom1).isNotEqualTo(classroom2);
    }
}
