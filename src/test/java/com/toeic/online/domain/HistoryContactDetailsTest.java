package com.toeic.online.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.toeic.online.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoryContactDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoryContactDetails.class);
        HistoryContactDetails historyContactDetails1 = new HistoryContactDetails();
        historyContactDetails1.setId(1L);
        HistoryContactDetails historyContactDetails2 = new HistoryContactDetails();
        historyContactDetails2.setId(historyContactDetails1.getId());
        assertThat(historyContactDetails1).isEqualTo(historyContactDetails2);
        historyContactDetails2.setId(2L);
        assertThat(historyContactDetails1).isNotEqualTo(historyContactDetails2);
        historyContactDetails1.setId(null);
        assertThat(historyContactDetails1).isNotEqualTo(historyContactDetails2);
    }
}
