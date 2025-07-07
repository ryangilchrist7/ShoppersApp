package com.shoppersapp.demo.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.User;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.factory.DebitCardFactory;

class DebitCardFactoryTest {
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
    private final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(0.0);
    private final BigDecimal INITIAL_INTEREST_ACCRUED = BigDecimal.valueOf(0.0);
    private final BankAccount VALID_BANK_ACCOUNT = new BankAccount(VALID_BANK_ACCOUNT_ID,
            VALID_USER,
            VALID_INTEREST_ISSUE,
            INITIAL_BALANCE,
            INITIAL_INTEREST_ACCRUED);

    @Test
    void createDebitCard_withValidDetails_shouldReturnDebitCard() {
        DebitCard card = DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, VALID_DEBIT_CARD_ID,
                VALID_EXPIRATION_DATE);

        assertNotNull(card);
    }

    @Test
    void createDebitCard_withInvalidCardNumber_shouldThrowException() {
        DebitCardId invalidCardId = new DebitCardId("1234", VALID_CVV);

        assertThrows(IllegalArgumentException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, invalidCardId, VALID_EXPIRATION_DATE);
        });
    }

    @Test
    void createDebitCard_withInVALID_CVV_shouldThrowException() {
        DebitCardId invalidCardId = new DebitCardId(VALID_LONG_CARD_NUMBER, "12");

        assertThrows(IllegalArgumentException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, invalidCardId, VALID_EXPIRATION_DATE);
        });
    }

    @Test
    void createDebitCard_withInVALID_EXPIRATION_DATE_shouldThrowException() {
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, VALID_DEBIT_CARD_ID, pastDate);
        });
    }

    @Test
    void createDebitCard_withNullValues_shouldThrowNullPointerException() {

        assertThrows(NullPointerException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, null, VALID_EXPIRATION_DATE);
        });

        assertThrows(NullPointerException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, VALID_DEBIT_CARD_ID, null);
        });

        assertThrows(NullPointerException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, new DebitCardId(null, VALID_CVV),
                    VALID_EXPIRATION_DATE);
        });

        assertThrows(NullPointerException.class, () -> {
            DebitCardFactory.createDebitCard(VALID_BANK_ACCOUNT, new DebitCardId(VALID_LONG_CARD_NUMBER, null),
                    VALID_EXPIRATION_DATE);
        });
    }
}