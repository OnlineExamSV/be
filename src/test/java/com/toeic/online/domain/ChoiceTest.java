package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChoiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Choice.class);
        Choice choice1 = new Choice();
        choice1.setId(1L);
        Choice choice2 = new Choice();
        choice2.setId(choice1.getId());
        assertThat(choice1).isEqualTo(choice2);
        choice2.setId(2L);
        assertThat(choice1).isNotEqualTo(choice2);
        choice1.setId(null);
        assertThat(choice1).isNotEqualTo(choice2);
    }
}
