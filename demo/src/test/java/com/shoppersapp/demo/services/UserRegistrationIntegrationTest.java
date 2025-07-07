package com.shoppersapp.demo.services;

import com.shoppersapp.services.*;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.repositories.*;
import com.shoppersapp.model.User;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.ShoppersAppApplication;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.InterestIssueFactory;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = ShoppersAppApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRegistrationIntegrationTest {
        private Connection connection;
        @Autowired
        private DataSource dataSource;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private BankAccountRepository bankAccountRepository;
        @Autowired
        private InterestIssueRepository interestIssueRepository;
        @Autowired
        private DebitCardRepository debitCardRepository;
        @Autowired
        private UserRegistrationService userRegistrationService;
        @Autowired
        private BankAccountService bankAccountService;
        @Autowired
        private DebitCardService debitCardService;
        private String VALID_FIRST = "Alice";
        private String VALID_LAST = "Smith";
        private LocalDate VALID_DOB = LocalDate.of(1995, 1, 1);
        private String VALID_PHONE = "+1234567890";
        private String VALID_EMAIL = "user@example.com";
        private String VALID_ADDRESS = "10 Test Street";
        private String VALID_PASS = "securepassword123";

        @BeforeEach
        public void cleanDatabase() throws SQLException {
                this.connection = dataSource.getConnection();

                try (Statement stmt = connection.createStatement()) {
                        stmt.execute("TRUNCATE TABLE transactions, cards, bankaccounts, accounts, interestissues CASCADE");
                }
                InterestIssue interestIssue = InterestIssueFactory.createInterestIssue(BigDecimal.valueOf(1.0));
                this.interestIssueRepository.save(interestIssue);
        }

        @Test
        public void testUserRegistrationSuccess() throws SQLException {
                try {
                        userRegistrationService.registerUser(
                                        VALID_FIRST,
                                        VALID_DOB,
                                        VALID_LAST,
                                        VALID_PHONE,
                                        VALID_ADDRESS,
                                        VALID_EMAIL,
                                        VALID_PASS);

                        User registeredUser = this.userRepository.findByEmailOrPhoneNumber(VALID_EMAIL, VALID_PHONE)
                                        .orElseThrow(() -> new RuntimeException("User not found"));
                        BankAccountId bankAccountId = bankAccountRepository
                                        .findByUser_UserId(registeredUser.getUserId())
                                        .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                        .getBankAccountId();

                        assertNotNull(
                                        debitCardRepository.findByBankAccount_BankAccountId(bankAccountId)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Debit card not found")));
                } catch (NullPointerException e) {
                        e.printStackTrace();

                        fail("NullPointerException thrown during user registration: " + e.getMessage());
                } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception during user registration: " + e.getMessage());
                }
        }

        @Test
        public void

                        testUserRegistration_duplicatePhone_throwsIdentifierInUseException()
                                        throws SQLException, IdentifierInUseException {
                userRegistrationService.registerUser(
                                VALID_FIRST,
                                VALID_DOB,
                                VALID_LAST,
                                VALID_PHONE,
                                VALID_ADDRESS,
                                VALID_EMAIL,
                                VALID_PASS);

                assertThrows(IdentifierInUseException.class, () -> {
                        userRegistrationService.registerUser(
                                        VALID_FIRST,
                                        VALID_DOB,
                                        VALID_LAST,
                                        VALID_PHONE,
                                        VALID_ADDRESS,
                                        "testuser@sample.com",
                                        VALID_PASS);
                });
        }

        @Test
        public void testUserRegistration_duplicateEmail_throwsIdentifierInUseException()
                        throws SQLException, IdentifierInUseException {
                userRegistrationService.registerUser(
                                VALID_FIRST,
                                VALID_DOB,
                                VALID_LAST,
                                VALID_PHONE,
                                VALID_ADDRESS,
                                VALID_EMAIL,
                                VALID_PASS);

                assertThrows(IdentifierInUseException.class, () -> {
                        userRegistrationService.registerUser(
                                        VALID_FIRST,
                                        VALID_DOB,
                                        VALID_LAST,
                                        "+0234567890",
                                        VALID_ADDRESS,
                                        VALID_EMAIL,
                                        VALID_PASS);
                });
        }

        @Test
        void testRegistration_Success_RetriesOnCollisionAccountNumber() throws Exception {
                AtomicBoolean firstCall = new AtomicBoolean(true);

                BankAccountService spyBankAccountService = Mockito.spy(bankAccountService);

                when(spyBankAccountService.generateAccountNumber()).thenAnswer(invocation -> {
                        if (firstCall.getAndSet(false)) {
                                return "87654321";
                        } else {
                                return "12345678";
                        }
                });

                try (MockedStatic<PasswordUtils> mockPasswordUtils = Mockito.mockStatic(PasswordUtils.class)) {
                        mockPasswordUtils.when(() -> PasswordUtils.hashPassword("password123"))
                                        .thenReturn(new byte[] { 1, 2, 3 });
                        this.userRegistrationService.registerUser(
                                        "Jane",
                                        LocalDate.of(2000, 1, 1),
                                        "Doe",
                                        "+14155552671",
                                        "123 Main St",
                                        "jane@example.com",
                                        "password123");
                }

                User firstUser = this.userRepository.findByEmail("jane@example.com")
                                .orElseThrow(() -> new RuntimeException("User not found with email jane@example.com"));
                assertNotNull(firstUser, "User should be registered and retrievable.");
                BankAccountId bankAccountId = bankAccountRepository
                                .findByUser_UserId(firstUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                .getBankAccountId();

                assertNotNull(debitCardRepository.findByBankAccount_BankAccountId(bankAccountId)
                                .orElseThrow(() -> new RuntimeException("Debit card not found")));

                firstCall.set(true);

                try (MockedStatic<PasswordUtils> mockPasswordUtils = Mockito.mockStatic(PasswordUtils.class)) {
                        mockPasswordUtils.when(() -> PasswordUtils.hashPassword(VALID_PASS))
                                        .thenReturn(new byte[] { 1, 2, 3 });

                        this.userRegistrationService.registerUser(
                                        VALID_FIRST,
                                        VALID_DOB,
                                        VALID_LAST,
                                        VALID_PHONE,
                                        VALID_ADDRESS,
                                        VALID_EMAIL,
                                        VALID_PASS);
                }

                User secondUser = this.userRepository.findByEmail(VALID_EMAIL)
                                .orElseThrow(() -> new RuntimeException("User not found with email " + VALID_EMAIL));
                assertNotNull(secondUser, "User should be registered and retrievable.");
                BankAccountId bankAccountId_2 = bankAccountRepository
                                .findByUser_UserId(secondUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                .getBankAccountId();
                assertNotNull(debitCardRepository.findByBankAccount_BankAccountId(bankAccountId_2)
                                .orElseThrow(() -> new RuntimeException("Debit card not found")));
        }

        @Test
        void testRegistration_Success_RetriesOnCollisionDebitCard() throws Exception {
                AtomicBoolean lFirstCall = new AtomicBoolean(true);
                AtomicBoolean cFirstCall = new AtomicBoolean(true);

                DebitCardService spyDebitCardService = Mockito.spy(debitCardService);

                doAnswer(invocation -> {
                        if (cFirstCall.getAndSet(false)) {
                                return "1234567812345678";
                        } else {
                                return invocation.callRealMethod();
                        }
                }).when(spyDebitCardService).generateLongCardNumber();

                doAnswer(invocation -> {
                        if (cFirstCall.getAndSet(false)) {
                                return "123";
                        } else {
                                return invocation.callRealMethod();
                        }
                }).when(spyDebitCardService).generateCVV();

                try (MockedStatic<PasswordUtils> mockPasswordUtils = Mockito.mockStatic(PasswordUtils.class)) {
                        mockPasswordUtils.when(() -> PasswordUtils.hashPassword("password123"))
                                        .thenReturn(new byte[] { 1, 2, 3 });
                        this.userRegistrationService.registerUser(
                                        "Jane",
                                        LocalDate.of(2000, 1, 1),
                                        "Doe",
                                        "+14155552671",
                                        "123 Main St",
                                        "jane@example.com",
                                        "password123");
                }

                User firstUser = this.userRepository.findByEmail("jane@example.com")
                                .orElseThrow(() -> new RuntimeException("No user with that email found"));
                assertNotNull(firstUser, "User should be registered and retrievable.");
                BankAccount bankAccount = bankAccountRepository.findByUser_UserId(firstUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"));
                assertNotNull(bankAccount, "Bank account should exist for user");

                DebitCard debitCard = debitCardRepository
                                .findByBankAccount_BankAccountId(bankAccount.getBankAccountId())
                                .orElseThrow(() -> new RuntimeException("Debit Card not found"));
                assertNotNull(debitCard);

                lFirstCall.set(true);
                cFirstCall.set(true);

                try (MockedStatic<PasswordUtils> mockPasswordUtils = Mockito.mockStatic(PasswordUtils.class)) {
                        mockPasswordUtils.when(() -> PasswordUtils.hashPassword(VALID_PASS))
                                        .thenReturn(new byte[] { 1, 2, 3 });

                        this.userRegistrationService.registerUser(
                                        VALID_FIRST,
                                        VALID_DOB,
                                        VALID_LAST,
                                        VALID_PHONE,
                                        VALID_ADDRESS,
                                        VALID_EMAIL,
                                        VALID_PASS);
                }

                User secondUser = this.userRepository.findByEmail(VALID_EMAIL)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                BankAccountId bankAccountId_2 = bankAccountRepository
                                .findByUser_UserId(secondUser.getUserId())
                                .orElseThrow(() -> new RuntimeException("Bank account not found"))
                                .getBankAccountId();
                assertNotNull(
                                debitCardRepository.findByBankAccount_BankAccountId(bankAccountId_2)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Debit card not found")));
        }
}