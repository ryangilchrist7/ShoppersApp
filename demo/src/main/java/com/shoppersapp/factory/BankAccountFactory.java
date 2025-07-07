package com.shoppersapp.factory;

import java.math.BigDecimal;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.User;

/**
 * Factory for bankaccount objects.
 * Responsible for creating bankaccount objects.
 */
public class BankAccountFactory {

    /**
     * Creates a new bank account object.
     * 
     * @return a new BankAccount object or null if the details are not valid
     */
    public static BankAccount createBankAccount(BankAccountId bankAccountId, User user,
            InterestIssue interestIssue,
            BigDecimal balance, BigDecimal interestAccrued) {
        if (isValidBankAccount(bankAccountId.getAccountNumber(), bankAccountId.getSortCode(), user.getUserId(),
                interestIssue.getInterestIssueId(),
                balance, interestAccrued)) {
            return new BankAccount(bankAccountId, user, interestIssue, balance,
                    interestAccrued);
        }
        return null;
    }

    /**
     * Validates a given bank account's proposed details.
     * 
     * @throws NullPointerException if any passed value is null
     */
    private static boolean isValidBankAccount(String accountNumber, String sortCode, Integer userId,
            Integer interestIssueId, BigDecimal balance, BigDecimal interestAccrued) throws NullPointerException {
        if (accountNumber == null || sortCode == null || userId == null || interestIssueId == null || balance == null
                || interestAccrued == null) {
            throw new NullPointerException("Null value detected in bank account details");
        }
        return (isValidBankAccountDetails(accountNumber, sortCode) && isValidID(userId, interestIssueId));

    }

    /**
     * @return true if the account number is valid
     * @throws IllegalArgumentExcept if the accountNumber is not exactly 8 numeric
     *                               characters
     */
    private static boolean isValidBankAccountDetails(String accountNumber, String sortCode) {
        if (accountNumber.matches("^\\d{8}$") && sortCode.matches("^\\d{6}$")) {
            return true;
        } else {
            throw new IllegalArgumentException(
                    "Account numbers and Sort codes must be exactly 8/6 numeric characters exactly");
        }
    }

    /**
     * @return true if the ID values are valid
     * @throws IllegalArgumentException if the ID values are negative
     */
    private static boolean isValidID(Integer userId, Integer interestIssueId) {
        if (userId > 0 && interestIssueId > 0) {
            return true;
        } else {
            throw new IllegalArgumentException("ID values must be positive");
        }
    }
}
