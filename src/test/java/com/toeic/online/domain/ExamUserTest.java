package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExamUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExamUser.class);
        ExamUser examUser1 = new ExamUser();
        examUser1.setId(1L);
        ExamUser examUser2 = new ExamUser();
        examUser2.setId(examUser1.getId());
        assertThat(examUser1).isEqualTo(examUser2);
        examUser2.setId(2L);
        assertThat(examUser1).isNotEqualTo(examUser2);
        examUser1.setId(null);
        assertThat(examUser1).isNotEqualTo(examUser2);
    }
}
