package com.shoppersapp.demo.services;

import com.shoppersapp.model.*;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;
import com.shoppersapp.repositories.TransactionRepository;

import com.shoppersapp.services.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

public class PurchaseServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private DebitCardRepository debitCardRepository;
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        purchaseService = new PurchaseService(bankAccountRepository, debitCardRepository, transactionRepository);
    }

    @Test
    void testPurchaseWithValidCardAndSufficientInterest() throws SQLException {
        DebitCardId cardId = new DebitCardId("1234567890123456", "123");
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId = new BankAccountId("1", "00-11-22");
        BankAccount account = new BankAccount(bankAccountId, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("5.00"));

        DebitCard card = new DebitCard(cardId, account, LocalDate.of(2000, 1, 1));

        when(debitCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(account));

        purchaseService.purchase(card, new BigDecimal("10.00"));

        verify(bankAccountRepository, atLeastOnce()).save(any(BankAccount.class));
        verify(transactionRepository, atLeast(1)).save(any(Transaction.class));
    }

    @Test
    void testPurchaseWithZeroAmountThrowsException() {
        DebitCardId cardId = new DebitCardId("1234567890123456", "123");
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId = new BankAccountId("1", "00-11-22");
        BankAccount account = new BankAccount(bankAccountId, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("5.00"));

        DebitCard card = new DebitCard(cardId, account, LocalDate.of(2000, 1, 1));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.purchase(card, BigDecimal.ZERO);
        });
        assertEquals("Purchase amount must be greater than 0.", exception.getMessage());
    }

    @Test
    void testPurchaseWithUnknownCardThrowsException() {
        when(debitCardRepository.findById(any(DebitCardId.class))).thenReturn(Optional.empty());
        DebitCardId cardId = new DebitCardId("1234567890123456", "123");
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId = new BankAccountId("1", "00-11-22");
        BankAccount account = new BankAccount(bankAccountId, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("5.00"));
        DebitCard card = new DebitCard(cardId, account, LocalDate.of(2000, 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            purchaseService.purchase(card, new BigDecimal("5.00"));
        });
        assertEquals("No card with those details exist", exception.getMessage());
    }

    @Test
    void testRewardCappedAtInterestAccrued() throws SQLException {
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId = new BankAccountId("1", "00-11-22");
        BankAccount account = new BankAccount(bankAccountId, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("0.03"));
        DebitCardId cardId = new DebitCardId("1234567890123456", "123");
        DebitCard card = new DebitCard(cardId, account, LocalDate.of(2000, 1, 1));
        when(debitCardRepository.findById(cardId)).thenReturn(Optional.of(card));

        when(bankAccountRepository.findById(bankAccountId)).thenReturn(Optional.of(account));

        purchaseService.purchase(card, new BigDecimal("10.00"));

        verify(bankAccountRepository, atLeastOnce()).save(any(BankAccount.class));
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }
}