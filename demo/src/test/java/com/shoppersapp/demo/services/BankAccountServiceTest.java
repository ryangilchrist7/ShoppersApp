package com.shoppersapp.demo.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.shoppersapp.services.BankAccountService;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.User;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.InterestIssueRepository;

import java.util.Optional;

public class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private InterestIssueRepository interestIssueRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private User mockUser;
    private InterestIssue mockInterestIssue;
    private BankAccount duplicateAccount;

    @BeforeEach
    void setup() throws SQLException {
        MockitoAnnotations.openMocks(this);

        mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn(123);

        mockInterestIssue = mock(InterestIssue.class);
        when(mockInterestIssue.getInterestIssueId()).thenReturn(999);

        duplicateAccount = mock(BankAccount.class);
    }

    @Test
    void testGenerateBankAccountDetails_UniqueAccountGenerated() throws SQLException {
        when(interestIssueRepository.findLatestInterestIssue())
                .thenReturn(mockInterestIssue);

        when(bankAccountRepository.findById(any(BankAccountId.class))).thenReturn(Optional.empty());

        BankAccount result = bankAccountService.generateBankAccountDetails(mockUser);

        assertNotNull(result);
        assertEquals(mockUser.getUserId(), result.getUser().getUserId());

        verify(bankAccountRepository, atLeastOnce()).findById(any(BankAccountId.class));
    }

    @Test
    void testGenerateBankAccountDetails_RetriesOnDuplicate() throws SQLException {
        when(interestIssueRepository.findLatestInterestIssue())
                .thenReturn(mockInterestIssue);

        when(bankAccountRepository.findById(any(BankAccountId.class)))
                .thenReturn(Optional.of(duplicateAccount))
                .thenReturn(Optional.empty());

        BankAccount result = bankAccountService.generateBankAccountDetails(mockUser);

        assertNotNull(result);
        assertEquals(mockUser.getUserId(), result.getUser().getUserId());

        verify(bankAccountRepository, atLeast(2)).findById(any(BankAccountId.class));
    }

    @Test
    public void testAccrueInterestForAllAccounts_success() throws SQLException {
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId1 = new BankAccountId("1", "2");
        BankAccountId bankAccountId2 = new BankAccountId("2", "1");
        BankAccount account1 = new BankAccount(bankAccountId1, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("0.00"));
        BankAccount account2 = new BankAccount(bankAccountId2, dummyUser, interestIssue, new BigDecimal("200.00"),
                new BigDecimal("5.00"));

        List<BankAccount> mockAccounts = Arrays.asList(account1, account2);
        when(bankAccountRepository.findAll()).thenReturn(mockAccounts);

        bankAccountService.accrueInterestForAllAccounts();

        BigDecimal expected1 = new BigDecimal("5.00");
        BigDecimal actual1 = account1.getInterestAccrued();
        BigDecimal delta1 = actual1.subtract(expected1).abs();
        assertTrue(delta1.compareTo(new BigDecimal("0.001")) < 0, "Expected ~5.00 but got " + actual1);

        BigDecimal expected2 = new BigDecimal("15.00");
        BigDecimal actual2 = account2.getInterestAccrued();
        BigDecimal delta2 = actual2.subtract(expected2).abs();
        assertTrue(delta2.compareTo(new BigDecimal("0.001")) < 0, "Expected ~15.00 but got " + actual2);

        verify(bankAccountRepository, times(1)).save(account1);
        verify(bankAccountRepository, times(1)).save(account2);
    }

    @Test
    public void testAccrueInterestForAllAccounts_repositoryThrowsException() throws SQLException {
        User dummyUser = new User();
        InterestIssue interestIssue = new InterestIssue();
        BankAccountId bankAccountId1 = new BankAccountId("1", "2");
        BankAccount account1 = new BankAccount(bankAccountId1, dummyUser, interestIssue, new BigDecimal("100.00"),
                new BigDecimal("0.00"));
        when(bankAccountRepository.findAll()).thenReturn(List.of(account1));
        doThrow(new RuntimeException("Error accruing interest")).when(bankAccountRepository).save(account1);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            bankAccountService.accrueInterestForAllAccounts();
        });

        assertEquals("Error accruing interest", thrown.getMessage());
    }
}