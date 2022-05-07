package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryContactTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoryContact.class);
        HistoryContact historyContact1 = new HistoryContact();
        historyContact1.setId(1L);
        HistoryContact historyContact2 = new HistoryContact();
        historyContact2.setId(historyContact1.getId());
        assertThat(historyContact1).isEqualTo(historyContact2);
        historyContact2.setId(2L);
        assertThat(historyContact1).isNotEqualTo(historyContact2);
        historyContact1.setId(null);
        assertThat(historyContact1).isNotEqualTo(historyContact2);
    }
}
