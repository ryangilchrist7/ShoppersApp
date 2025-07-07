package com.shoppersapp.demo.factory;

import com.shoppersapp.model.*;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.factory.TransactionFactory;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionFactoryTest {
    private final String VALID_LONG_CARD_NUMBER = "1234567812345678";
    private final String VALID_CVV = "123";
    private final LocalDate VALID_EXPIRATION_DATE = LocalDate.now().plusYears(2);
    private final DebitCardId VALID_DEBIT_CARD_ID = new DebitCardId(VALID_LONG_CARD_NUMBER, VALID_CVV);
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
    private final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000.0);
    private final BigDecimal INITIAL_INTEREST_ACCRUED = BigDecimal.valueOf(0.0);
    private final BankAccount VALID_BANK_ACCOUNT = new BankAccount(VALID_BANK_ACCOUNT_ID,
            VALID_USER,
            VALID_INTEREST_ISSUE,
            INITIAL_BALANCE,
            INITIAL_INTEREST_ACCRUED);
    private final BigDecimal amount = new BigDecimal("100.00");
    private final Timestamp now = new Timestamp(System.currentTimeMillis());
    private final DebitCard VALID_DEBIT_CARD = new DebitCard(VALID_DEBIT_CARD_ID, VALID_BANK_ACCOUNT,
            VALID_EXPIRATION_DATE);

    // --- SUCCESS CASES ---

    @Test
    public void testCreateDepositTransactionSuccess() {
        Transaction tx = TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("1100.00"),
                TransactionType.DEPOSIT,
                now);
        assertEquals(TransactionType.DEPOSIT, tx.getTransactionType());
    }

    @Test
    public void testCreateWithdrawalTransactionSuccess() {
        Transaction tx = TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("900.00"),
                TransactionType.WITHDRAWAL,
                now);
        assertEquals(TransactionType.WITHDRAWAL, tx.getTransactionType());
    }

    @Test
    public void testCreateRewardTransactionSuccess() {
        Transaction tx = TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("1100.00"),
                TransactionType.REWARD,
                now);
        assertEquals(TransactionType.REWARD, tx.getTransactionType());
    }

    @Test
    public void testCreatePurchaseTransactionSuccess() {
        Transaction tx = TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, VALID_DEBIT_CARD,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("900.00"),
                TransactionType.PURCHASE,
                now);
        assertEquals(TransactionType.PURCHASE, tx.getTransactionType());
    }

    // --- FAILURE CASES ---

    @Test
    public void testNullBankAccountIdThrows() {
        assertThrows(NullPointerException.class, () -> TransactionFactory.createTransaction(
                null, null, null,
                amount,
                BigDecimal.TEN, BigDecimal.ONE,
                TransactionType.DEPOSIT, now));
    }

    @Test
    public void testNullAmountThrows() {
        assertThrows(NullPointerException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                null,
                BigDecimal.TEN, BigDecimal.ONE,
                TransactionType.DEPOSIT, now));
    }

    @Test
    public void testNegativeAmountThrows() {
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                new BigDecimal("-50.00"),
                BigDecimal.TEN, BigDecimal.ONE,
                TransactionType.DEPOSIT, now));
    }

    @Test
    public void testNullTransactionTypeThrows() {
        assertThrows(NullPointerException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                BigDecimal.TEN, BigDecimal.ONE,
                null, now));
    }

    @Test
    public void testNullDebitCardIdForPurchaseThrows() {
        assertThrows(NullPointerException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("900.00"),
                TransactionType.PURCHASE,
                now));
    }

    @Test
    public void testNonNullDebitCardIdForNonPurchaseThrows() {
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, VALID_DEBIT_CARD,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("1100.00"),
                TransactionType.DEPOSIT,
                now));
    }

    @Test
    public void testInvalidLogicForDepositThrows() {
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, null,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("900.00"),
                TransactionType.DEPOSIT,
                now));
    }

    @Test
    public void testInvalidLogicForPurchaseThrows() {
        assertThrows(IllegalArgumentException.class, () -> TransactionFactory.createTransaction(
                null, VALID_BANK_ACCOUNT, VALID_DEBIT_CARD,
                amount,
                new BigDecimal("1000.00"),
                new BigDecimal("1100.00"),
                TransactionType.PURCHASE,
                now));
    }
}