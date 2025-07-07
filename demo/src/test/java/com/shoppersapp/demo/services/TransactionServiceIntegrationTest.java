package com.shoppersapp.demo.services;

import com.shoppersapp.services.*;
import com.shoppersapp.repositories.*;
import com.shoppersapp.model.*;
import com.shoppersapp.ShoppersAppApplication;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.InterestIssueFactory;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

@SpringBootTest(classes = ShoppersAppApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceIntegrationTest {
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
        private WithdrawalService withdrawalService;

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
        public void testTransactionServiceNoTransactions() {
                BankAccountId bankAccountId = bankAccountRepository.findByUser_UserId(testUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                .getBankAccountId();

                assertTrue(
                                transactionRepository.findAllTransactionsByBankAccountId(
                                                bankAccountId.getAccountNumber(),
                                                bankAccountId.getSortCode()).isEmpty(),
                                "Expected no transactions for this bank account");
        }

        @Test
        public void testTransactionServiceManyTransaction() throws SQLException {
                BankAccountId bankAccountId = bankAccountRepository.findByUser_UserId(testUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                .getBankAccountId();
                BigDecimal initialDeposit = new BigDecimal("10000.00");
                depositService.deposit(bankAccountId, initialDeposit);

                DebitCard debitCard = debitCardRepository.findByBankAccount_BankAccountId(bankAccountId)
                                .orElseThrow(() -> new RuntimeException("Debit card not found"));

                BigDecimal transactionAmount = new BigDecimal("50.00");

                for (int i = 0; i < 100; i++) {
                        if (i % 2 == 0) {
                                this.withdrawalService.withdraw(bankAccountId,
                                                transactionAmount);
                        } else {
                                purchaseService.purchase(debitCard, transactionAmount);
                        }
                }

                List<Transaction> transactions = transactionRepository.findAllTransactionsByBankAccountId(
                                bankAccountId.getAccountNumber(),
                                bankAccountId.getSortCode());

                assertEquals(101, transactions.size(), "Expected 100 transactions plus the initial deposit");
        }
}