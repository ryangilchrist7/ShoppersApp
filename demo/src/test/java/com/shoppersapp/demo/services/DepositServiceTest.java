package com.shoppersapp.demo.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.shoppersapp.model.*;
import com.shoppersapp.repositories.TransactionRepository;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.services.DepositService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

public class DepositServiceTest {

    private BankAccountRepository mockBankAccountRepository;
    private TransactionRepository mockTransactionRepository;
    private DepositService depositService;

    private BankAccountId sampleId;
    private BankAccount mockAccount;

    @BeforeEach
    public void setup() {
        mockBankAccountRepository = mock(BankAccountRepository.class);
        mockTransactionRepository = mock(TransactionRepository.class);
        depositService = new DepositService(mockBankAccountRepository, mockTransactionRepository);
        sampleId = new BankAccountId("12345678", "000000");
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        mockAccount = new BankAccount(sampleId, dummyUser, interestIssue, new BigDecimal(100.00), new BigDecimal(0));

        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));
    }

    @Test
    public void testSuccessfulDeposit() throws SQLException {
        BigDecimal depositAmount = BigDecimal.valueOf(50.00);

        depositService.deposit(sampleId, depositAmount);

        assertEquals(BigDecimal.valueOf(150.00), mockAccount.getBalance());
        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository).save(any());
    }

    @Test
    public void testZeroDepositThrowsException() throws SQLException {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> depositService.deposit(sampleId, BigDecimal.ZERO));

        assertEquals("Deposit amount must be greater than 0.", thrown.getMessage());
        verify(mockBankAccountRepository, never()).save(any());
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testNegativeDepositThrowsException() throws SQLException {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> depositService.deposit(sampleId, BigDecimal.valueOf(-10)));

        assertEquals("Deposit amount must be greater than 0.", thrown.getMessage());
        verify(mockBankAccountRepository, never()).save(any());
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testSaveThrowsExceptionButCaught() throws SQLException {
        doThrow(new RuntimeException("DB save failed")).when(mockBankAccountRepository).save(mockAccount);

        // Should not throw to caller
        assertDoesNotThrow(() -> depositService.deposit(sampleId, BigDecimal.valueOf(50)));

        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testLogDepositThrowsSQLExceptionButCaught() throws SQLException {
        doThrow(new RuntimeException("Logging failed")).when(mockTransactionRepository).save(any());

        assertDoesNotThrow(() -> depositService.deposit(sampleId, BigDecimal.valueOf(50)));

        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository).save(any());
    }
}