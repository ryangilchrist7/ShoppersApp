package com.shoppersapp.demo.services;

import com.shoppersapp.services.*;
import com.shoppersapp.repositories.*;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.Transaction;
import com.shoppersapp.model.User;
import com.shoppersapp.ShoppersAppApplication;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.InterestIssueFactory;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShoppersAppApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PurchaseServiceIntegrationTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private InterestIssueRepository interestIssueRepository;

    private static final String VALID_FIRST = "Alice";
    private static final String VALID_LAST = "Smith";
    private static final LocalDate VALID_DOB = LocalDate.of(1995, 1, 1);
    private static final String VALID_PHONE = "+1234567890";
    private static final String VALID_EMAIL = "testuser@example.com";
    private static final String VALID_ADDRESS = "10 Test Street";
    private static final String VALID_PASS = "securepassword123";

    private User testUser;
    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        this.connection = dataSource.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE transactions, cards, bankaccounts, accounts, interestissues CASCADE");
        }

        InterestIssue interestIssue = InterestIssueFactory.createInterestIssue(BigDecimal.valueOf(1.0));
        this.interestIssueRepository.save(interestIssue);

        try {
            userRegistrationService.registerUser(
                    VALID_FIRST,
                    VALID_DOB,
                    VALID_LAST,
                    VALID_PHONE,
                    VALID_ADDRESS,
                    VALID_EMAIL,
                    VALID_PASS);
        } catch (IdentifierInUseException e) {
            System.err.println("User already exists during test setup: " + e.getMessage());
        }

        this.testUser = userRepository.findByEmail(VALID_EMAIL)
                .orElseThrow(() -> new RuntimeException("User not initialised successfully"));
    }

    @Test
    public void testPurchaseSuccessNoReward() throws SQLException {
        BankAccount retrievedBankAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not initialised properly"));
        this.depositService.deposit(retrievedBankAccount.getBankAccountId(), new BigDecimal("100.00"));

        BankAccount prePurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        DebitCard debitCard = this.debitCardRepository
                .findByBankAccount_BankAccountId(prePurchaseAccount.getBankAccountId())
                .orElseThrow(() -> new RuntimeException("Debit card not found"));

        this.purchaseService.purchase(debitCard, new BigDecimal("100.00"));

        BankAccount postPurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        List<Transaction> transactions = transactionRepository
                .findAllTransactionsByBankAccountId(postPurchaseAccount.getBankAccountId().getAccountNumber(),
                        postPurchaseAccount.getBankAccountId().getSortCode());
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty");
        assertEquals(0, postPurchaseAccount.getBalance().compareTo(new BigDecimal("0.00")));
        assertEquals(0, postPurchaseAccount.getInterestAccrued().compareTo(new BigDecimal("0.00")));
    }

    @Test
    public void testPurchaseSuccessMinimalReward() throws SQLException {
        BankAccount retrievedBankAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not initialised properly"));
        this.depositService.deposit(retrievedBankAccount.getBankAccountId(), new BigDecimal("100.00"));

        BankAccount retrievedBankAccount2 = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
        retrievedBankAccount2.setInterestAccrued(new BigDecimal("0.01"));
        bankAccountRepository.save(retrievedBankAccount2);

        BankAccount prePurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        DebitCard debitCard = this.debitCardRepository
                .findByBankAccount_BankAccountId(prePurchaseAccount.getBankAccountId())
                .orElseThrow(() -> new RuntimeException("Debit card not found"));

        this.purchaseService.purchase(debitCard, new BigDecimal("100.00"));

        BankAccount postPurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        List<Transaction> transactions = transactionRepository
                .findAllTransactionsByBankAccountId(postPurchaseAccount.getBankAccountId().getAccountNumber(),
                        postPurchaseAccount.getBankAccountId().getSortCode());
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty");
        assertEquals(3, transactions.size(), "There should be exactly 2 transactions");
        assertEquals(0, postPurchaseAccount.getBalance().compareTo(new BigDecimal("0.01")));
        assertEquals(0, postPurchaseAccount.getInterestAccrued().compareTo(new BigDecimal("0.00")));
    }

    @Test
    public void testPurchaseSuccessNormalReward() throws SQLException {
        BankAccount retrievedBankAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not initialised properly"));
        this.depositService.deposit(retrievedBankAccount.getBankAccountId(), new BigDecimal("100.00"));

        BankAccount retrievedBankAccount2 = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
        retrievedBankAccount2.setInterestAccrued(new BigDecimal("1.00"));
        bankAccountRepository.save(retrievedBankAccount2);

        BankAccount prePurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        DebitCard debitCard = this.debitCardRepository
                .findByBankAccount_BankAccountId(prePurchaseAccount.getBankAccountId())
                .orElseThrow(() -> new RuntimeException("Debit card not found"));

        this.purchaseService.purchase(debitCard, new BigDecimal("100.00"));

        BankAccount postPurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        List<Transaction> transactions = transactionRepository
                .findAllTransactionsByBankAccountId(postPurchaseAccount.getBankAccountId().getAccountNumber(),
                        postPurchaseAccount.getBankAccountId().getSortCode());
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty");
        assertEquals(3, transactions.size(), "There should be exactly 2 transactions");
        assertEquals(0, postPurchaseAccount.getBalance().compareTo(new BigDecimal("1.00")));
        assertEquals(0, postPurchaseAccount.getInterestAccrued().compareTo(new BigDecimal("0.00")));
    }

    @Test
    public void testPurchaseSuccessNormalRewardWithLeftovers() throws SQLException {
        BankAccount retrievedBankAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("User not initialised properly"));
        this.depositService.deposit(retrievedBankAccount.getBankAccountId(), new BigDecimal("100.00"));

        BankAccount retrievedBankAccount2 = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));
        retrievedBankAccount2.setInterestAccrued(new BigDecimal("2.00"));
        bankAccountRepository.save(retrievedBankAccount2);

        BankAccount prePurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        DebitCard debitCard = this.debitCardRepository
                .findByBankAccount_BankAccountId(prePurchaseAccount.getBankAccountId())
                .orElseThrow(() -> new RuntimeException("Debit card not found"));

        this.purchaseService.purchase(debitCard, new BigDecimal("100.00"));

        BankAccount postPurchaseAccount = this.bankAccountRepository.findByUser_UserId(testUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        List<Transaction> transactions = transactionRepository
                .findAllTransactionsByBankAccountId(postPurchaseAccount.getBankAccountId().getAccountNumber(),
                        postPurchaseAccount.getBankAccountId().getSortCode());
        assertFalse(transactions.isEmpty(), "Transaction list should not be empty");
        assertEquals(3, transactions.size(), "There should be exactly 2 transactions");
        assertEquals(0, postPurchaseAccount.getBalance().compareTo(new BigDecimal("1.00")));
        assertEquals(0, postPurchaseAccount.getInterestAccrued().compareTo(new BigDecimal("1.00")));
    }
}