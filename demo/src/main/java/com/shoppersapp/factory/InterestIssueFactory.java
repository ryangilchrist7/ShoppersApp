package com.shoppersapp.factory;

import java.math.BigDecimal;

import com.shoppersapp.model.InterestIssue;

/**
 * Factory for interest issue objects.
 */

public class InterestIssueFactory {
    /**
     * Creates a new interest issue object.
     * This function should only be used in the context of interest issue
     * generation.
     * 
     * @return a new InterestIssue object
     */
    public static InterestIssue createInterestIssue(BigDecimal interestRate) {
        if (isValidInterestRate(interestRate)) {
            return new InterestIssue(interestRate);
        }
        return null;
    }

    /**
     * Creates a new interest issue object.
     * This function should only be used in the context of retrieving an interest
     * issue from the database.
     * 
     * @return a new InterestIssue object
     */
    public static InterestIssue createInterestIssue(Integer interestIssueId, BigDecimal interestRate) {
        if (isValidInterestRate(interestRate) && isValidID(interestIssueId)) {
            return new InterestIssue(interestIssueId, interestRate);
        }
        return null;
    }

    /**
     * Validates interest rate
     * 
     * @return true if the interest rate is positive
     * @throws IllegalArgumentException if the rate is negative
     */
    private static boolean isValidInterestRate(BigDecimal interestRate) {
        if (interestRate.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        } else {
            throw new IllegalArgumentException("Interest Rate must be positive");
        }
    }

    /**
     * Validates the issue's id
     * 
     * @return true if the id is a positive integer
     * @throws IllegalArgumentException if the are values are negative
     */
    private static boolean isValidID(Integer interestIssueId) {
        if (interestIssueId > 0) {
            return true;
        } else {
            throw new IllegalArgumentException("ID values must be positive");
        }
    }

}
