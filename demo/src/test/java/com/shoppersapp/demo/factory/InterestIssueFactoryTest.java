package com.shoppersapp.demo.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.factory.InterestIssueFactory;

class InterestIssueFactoryTest {

    @Test
    void createInterestIssue_withValidInterestRate_shouldReturnIssue() {
        BigDecimal validRate = new BigDecimal("0.05");
        InterestIssue issue = InterestIssueFactory.createInterestIssue(validRate);

        assertNotNull(issue);
        assertEquals(validRate, issue.getInterestRate());
    }

    @Test
    void createInterestIssue_withInvalidInterestRate_throwsException() {
        BigDecimal zeroRate = BigDecimal.ZERO;
        BigDecimal negativeRate = new BigDecimal("-0.01");

        assertThrows(IllegalArgumentException.class, () -> {
            InterestIssueFactory.createInterestIssue(zeroRate);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            InterestIssueFactory.createInterestIssue(negativeRate);
        });
    }

    @Test
    void createInterestIssue_withValidIdAndValidInterestRate_shouldReturnIssue() {
        Integer id = 1;
        BigDecimal validRate = new BigDecimal("0.03");
        InterestIssue issue = InterestIssueFactory.createInterestIssue(id, validRate);

        assertNotNull(issue);
        assertEquals(id, issue.getInterestIssueId());
        assertEquals(validRate, issue.getInterestRate());
    }

    @Test
    void createInterestIssue_withValidIdAndInvalidInterestRate_throwsException() {
        Integer id = 2;
        BigDecimal zeroRate = BigDecimal.ZERO;

        assertThrows(IllegalArgumentException.class, () -> {
            InterestIssueFactory.createInterestIssue(id, zeroRate);
        });
    }

    @Test
    void createInterestIssue_withInvalidIdAndValidInterestRate_throwsException() {
        Integer id = -1;
        BigDecimal validRate = new BigDecimal("0.03");

        assertThrows(IllegalArgumentException.class, () -> {
            InterestIssueFactory.createInterestIssue(id, validRate);
        });
    }
}