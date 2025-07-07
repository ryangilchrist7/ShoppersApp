package com.shoppersapp.demo.services;

import com.shoppersapp.model.User;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.repositories.TransactionRepository;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.services.DepositService;
import com.shoppersapp.services.WithdrawalService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class WithdrawalServiceTest {

    private BankAccountRepository mockBankAccountRepository;
    private TransactionRepository mockTransactionRepository;
    private WithdrawalService withdrawalService;

    private BankAccountId sampleId;
    private BankAccount mockAccount;

    @BeforeEach
    public void setUp() {
        mockBankAccountRepository = mock(BankAccountRepository.class);
        mockTransactionRepository = mock(TransactionRepository.class);
        withdrawalService = new WithdrawalService(mockBankAccountRepository, mockTransactionRepository);
        sampleId = new BankAccountId("12345678", "000000");
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        mockAccount = new BankAccount(sampleId, dummyUser, interestIssue, new BigDecimal(200.00), new BigDecimal(0));

    }

    @Test
    public void testSuccessfulWithdrawal() throws SQLException {
        BigDecimal withdrawalAmount = BigDecimal.valueOf(50.00);
        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));
        withdrawalService.withdraw(sampleId, withdrawalAmount);

        assertEquals(BigDecimal.valueOf(150.00), mockAccount.getBalance());
        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository).save(any());
    }

    @Test
    public void testZeroWithdrawalThrowsException() throws SQLException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            withdrawalService.withdraw(sampleId, BigDecimal.ZERO);
        });
        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));

        assertEquals("Withdrawal amount must be greater than 0.", exception.getMessage());
        verify(mockBankAccountRepository, never()).save(any());
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testNegativeWithdrawalThrowsException() throws SQLException {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            withdrawalService.withdraw(sampleId, BigDecimal.valueOf(-10));
        });
        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));

        assertEquals("Withdrawal amount must be greater than 0.", exception.getMessage());
        verify(mockBankAccountRepository, never()).save(any());
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testSaveThrowsExceptionIsCaught() throws SQLException {
        doThrow(new RuntimeException("Save failed")).when(mockBankAccountRepository).save(any());
        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));

        assertDoesNotThrow(() -> withdrawalService.withdraw(sampleId, BigDecimal.valueOf(30)));

        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository, never()).save(any());
    }

    @Test
    public void testLogWithdrawalThrowsExceptionIsCaught() throws SQLException {
        doThrow(new RuntimeException("Insert failed")).when(mockTransactionRepository).save(any());
        when(mockBankAccountRepository.findById(sampleId)).thenReturn(Optional.of(mockAccount));

        assertDoesNotThrow(() -> withdrawalService.withdraw(sampleId, BigDecimal.valueOf(40)));

        verify(mockBankAccountRepository).save(mockAccount);
        verify(mockTransactionRepository).save(any());
    }
}