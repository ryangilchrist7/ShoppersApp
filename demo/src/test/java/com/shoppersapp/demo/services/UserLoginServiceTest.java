package com.shoppersapp.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.time.LocalDate;

import com.shoppersapp.services.UserLoginService;
import com.shoppersapp.repositories.UserRepository;
import com.shoppersapp.dto.UserLoginDTO;
import com.shoppersapp.model.User;
import com.shoppersapp.utils.DatabaseConnector;
import com.shoppersapp.utils.PasswordUtils;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

class UserLoginServiceTest {

    private Connection mockConnection;
    private UserRepository mockRepository;
    private UserLoginService userLoginService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PHONE = "1234567890";
    private static final String CORRECT_PASSWORD = "password123";
    private static final byte[] HASHED_PASSWORD = PasswordUtils.hashPassword("password123");

    @BeforeEach
    void setup() {
        mockConnection = mock(Connection.class);
        mockRepository = mock(UserRepository.class);
        userLoginService = new UserLoginService(mockRepository);
    }

    @Test
    void login_withCorrectEmailAndPassword_returnsDTO() throws Exception {
        try (MockedStatic<DatabaseConnector> mockedDb = mockStatic(DatabaseConnector.class);
                MockedStatic<PasswordUtils> mockedPw = mockStatic(PasswordUtils.class)) {

            mockedDb.when(DatabaseConnector::getConnection).thenReturn(mockConnection);

            User mockUser = new User(
                    1,
                    "a",
                    "b",
                    TEST_EMAIL,
                    TEST_PHONE,
                    LocalDate.of(2000, 1, 1),
                    "c",
                    HASHED_PASSWORD);
            UserLoginDTO mockDto = new UserLoginDTO(Integer.valueOf(1), "");

            when(mockRepository.findByEmailOrPhoneNumber(TEST_EMAIL, TEST_EMAIL))
                    .thenReturn(Optional.of(mockUser));
            mockedPw.when(() -> PasswordUtils.verifyPassword(CORRECT_PASSWORD, HASHED_PASSWORD)).thenReturn(true);

            UserLoginDTO result = userLoginService.login(TEST_EMAIL, CORRECT_PASSWORD);

            assertNotNull(result);
        }
    }

    @Test
    void login_withIncorrectPassword_returnsNull() throws Exception {
        try (MockedStatic<DatabaseConnector> mockedDb = mockStatic(DatabaseConnector.class);
                MockedStatic<PasswordUtils> mockedPw = mockStatic(PasswordUtils.class)) {

            mockedDb.when(DatabaseConnector::getConnection).thenReturn(mockConnection);
            whenNewRepositoryReturns(mockRepository);
            User mockUser = new User(
                    null,
                    "a",
                    "b",
                    TEST_EMAIL,
                    TEST_PHONE,
                    LocalDate.of(2000, 1, 1),
                    "c",
                    HASHED_PASSWORD);
            when(mockRepository.findByEmailOrPhoneNumber(TEST_EMAIL, TEST_PHONE))
                    .thenReturn(Optional.of(mockUser));
            mockedPw.when(() -> PasswordUtils.verifyPassword("wrongpassword", HASHED_PASSWORD)).thenReturn(false);

            UserLoginDTO result = userLoginService.login(TEST_EMAIL, "wrongpassword");

            assertNull(result);
        }
    }

    @Test
    void login_withUnknownIdentifier_returnsNull() {
        try (MockedStatic<DatabaseConnector> mockedDb = mockStatic(DatabaseConnector.class)) {
            mockedDb.when(DatabaseConnector::getConnection).thenReturn(mockConnection);
            whenNewRepositoryReturns(mockRepository);

            when(mockRepository.findByEmailOrPhoneNumber(TEST_EMAIL, TEST_PHONE))
                    .thenReturn(Optional.empty());
            UserLoginDTO result = userLoginService.login(TEST_PHONE, "anyPassword");

            assertNull(result);
        }
    }

    private void whenNewRepositoryReturns(UserRepository mockRepo) {
    }
}
