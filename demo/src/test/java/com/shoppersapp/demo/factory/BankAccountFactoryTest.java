package com.shoppersapp.demo.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.shoppersapp.model.User;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.factory.BankAccountFactory;

public class BankAccountFactoryTest {
    private final BankAccountId VALID_BANK_ACCOUNT_ID = new BankAccountId("12345678", "654321");
    private final byte[] VALID_PASSWORD_HASH = PasswordUtils.hashPassword("password123");
    private final User VALID_USER = new User(
            Integer.valueOf("1"),
            "John",
            "Doe",
            "john@example.com",
            "1234567890",
            LocalDate.of(2000, 1, 1),
            "123 Main St",
            VALID_PASSWORD_HASH);
    private final InterestIssue VALID_INTEREST_ISSUE = new InterestIssue(1, BigDecimal.valueOf(5.0));
    private final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(0.0);
    private final BigDecimal INITIAL_INTEREST_ACCRUED = BigDecimal.valueOf(0.0);

    @Test
    public void testCreateBankAccount_ValidInput_ShouldCreateBankAccount() {
        BankAccount account = BankAccountFactory.createBankAccount(VALID_BANK_ACCOUNT_ID,
                VALID_USER,
                VALID_INTEREST_ISSUE,
                INITIAL_BALANCE,
                INITIAL_INTEREST_ACCRUED);
        assertNotNull(account);
    }

    @Test
    public void testCreateBankAccount_NullAccountNumber_ShouldThrowNullPointerException() {
        BankAccountId invalidId = new BankAccountId(null, "87654321");
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(invalidId,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_NullSortCode_ShouldThrowNullPointerException() {
        BankAccountId invalidId = new BankAccountId("12345678", null);
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(invalidId,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_NullUser_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(VALID_BANK_ACCOUNT_ID,
                    null,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_NullInterestIssueId_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(VALID_BANK_ACCOUNT_ID,
                    VALID_USER,
                    null,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_NullBalance_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(VALID_BANK_ACCOUNT_ID,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    null,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_NullInterestAccrued_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            BankAccountFactory.createBankAccount(VALID_BANK_ACCOUNT_ID,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    null);
        });
    }

    @Test
    public void testCreateBankAccount_InvalidAccountNumber_ShouldThrowIllegalArgumentException() {
        BankAccountId invalidId = new BankAccountId("1234ABCD", "87654321");
        assertThrows(IllegalArgumentException.class, () -> {
            BankAccountFactory.createBankAccount(invalidId,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }

    @Test
    public void testCreateBankAccount_InvalidSortCode_ShouldThrowIllegalArgumentException() {
        BankAccountId invalidId = new BankAccountId("12345678", "8765ABCD"); // Invalid sort code (not all digits)
        assertThrows(IllegalArgumentException.class, () -> {
            BankAccountFactory.createBankAccount(invalidId,
                    VALID_USER,
                    VALID_INTEREST_ISSUE,
                    INITIAL_BALANCE,
                    INITIAL_INTEREST_ACCRUED);
        });
    }
}