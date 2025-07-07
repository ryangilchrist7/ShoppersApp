package com.shoppersapp.demo.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.shoppersapp.services.BankAccountService;
import com.shoppersapp.services.DebitCardService;
import com.shoppersapp.services.InterestIssueService;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.User;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.UserFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;
import com.shoppersapp.repositories.InterestIssueRepository;
import com.shoppersapp.repositories.UserRepository;
import com.shoppersapp.utils.PasswordUtils;

import java.util.Optional;

class UserRegistrationServiceTest {

        private UserRegistrationServiceTestClass userRegistrationService;
        private UserRepository userRepository;
        private BankAccountRepository bankAccountRepository;
        private DebitCardRepository debitCardRepository;
        private InterestIssueRepository interestIssueRepository;
        private BankAccountService bankAccountService;
        private DebitCardService debitCardService;
        private PasswordUtils passwordUtils;

        @BeforeEach
        void setup() {
                userRepository = mock(UserRepository.class);
                bankAccountRepository = mock(BankAccountRepository.class);
                debitCardRepository = mock(DebitCardRepository.class);
                interestIssueRepository = mock(InterestIssueRepository.class);
                bankAccountService = mock(BankAccountService.class);
                debitCardService = mock(DebitCardService.class);
                passwordUtils = mock(PasswordUtils.class);

                userRegistrationService = new UserRegistrationServiceTestClass(
                                userRepository,
                                bankAccountRepository,
                                debitCardRepository,
                                interestIssueRepository,
                                bankAccountService,
                                debitCardService,
                                passwordUtils);
        }

        @Test
        public void testRegisterUser_Success() throws Exception {
                String email = "john@example.com";
                String phone = "1234567890";

                User dummyUser = new User();
                dummyUser.setEmail(email);

                BankAccount dummyAccount = new BankAccount();
                DebitCard dummyCard = new DebitCard();

                when(userRepository.existsByEmail(email)).thenReturn(false);
                when(userRepository.existsByPhoneNumber(phone)).thenReturn(false);

                when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
                when(userRepository.findByEmail(email)).thenReturn(Optional.of(dummyUser));

                try (MockedStatic<UserFactory> mockedUserFactory = Mockito.mockStatic(UserFactory.class);
                                MockedStatic<PasswordUtils> mockedPasswordUtils = Mockito
                                                .mockStatic(PasswordUtils.class)) {

                        // Mock PasswordUtils.hashPassword
                        mockedPasswordUtils.when(() -> PasswordUtils.hashPassword("password123"))
                                        .thenReturn(new byte[] { 1, 2, 3 });

                        // Mock UserFactory.createUser to return dummyUser
                        mockedUserFactory.when(() -> UserFactory.createUser(
                                        anyString(), anyString(), any(LocalDate.class), anyString(), anyString(),
                                        anyString(), any()))
                                        .thenReturn(dummyUser);

                        when(bankAccountService.generateBankAccountDetails(dummyUser)).thenReturn(dummyAccount);
                        when(debitCardService.generateDebitCardDetails(dummyAccount)).thenReturn(dummyCard);

                        when(bankAccountRepository.save(dummyAccount)).thenReturn(null);
                        when(debitCardRepository.save(dummyCard)).thenReturn(null);

                        boolean result = userRegistrationService.registerUser(
                                        "John",
                                        LocalDate.of(2000, 1, 1),
                                        "Doe",
                                        phone,
                                        "123 Main St",
                                        email,
                                        "password123");

                        assertTrue(result);

                        verify(userRepository).existsByEmail(email);
                        verify(userRepository).existsByPhoneNumber(phone);
                        verify(userRepository).save(any(User.class));
                        verify(userRepository).findByEmail(email);
                        verify(bankAccountService).generateBankAccountDetails(dummyUser);
                        verify(debitCardService).generateDebitCardDetails(dummyAccount);
                        verify(bankAccountRepository).save(dummyAccount);
                        verify(debitCardRepository).save(dummyCard);
                }
        }

        @Test
        void testRegisterUser_ThrowsException_WhenPhoneNumberExists() throws SQLException {
                String phoneNumber = "1234567890";
                when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);
                IdentifierInUseException exception = assertThrows(IdentifierInUseException.class, () -> {
                        boolean result = userRegistrationService.registerUser("John", LocalDate.of(2000, 1, 1), "Doe",
                                        phoneNumber,
                                        "123 Street", "john@example.com", "securePassword123");
                });

                assertEquals("A user with that phone number or email address already exists.", exception.getMessage());
        }

        @Test
        void testRegisterUser_ThrowsException_WhenEmailExists() throws SQLException {
                String email = "john@example.com";
                String phoneNumber = "1234567890";
                when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
                when(userRepository.existsByEmail(email)).thenReturn(true);
                IdentifierInUseException exception = assertThrows(IdentifierInUseException.class, () -> {
                        boolean result = userRegistrationService.registerUser("John", LocalDate.of(2000, 1, 1), "Doe",
                                        "1234567890",
                                        "123 Street", email, "securePassword123");
                });
                assertEquals("A user with that phone number or email address already exists.", exception.getMessage());
        }

}
