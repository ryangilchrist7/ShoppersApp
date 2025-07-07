package com.shoppersapp.demo.factory;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.shoppersapp.model.User;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.factory.UserFactory;

public class UserFactoryTest {
        private final Integer VALID_ID = Integer.valueOf(1);
        private final String VALID_FIRST = "John";
        private final String VALID_LAST = "Doe";
        private final String VALID_EMAIL = "john@example.com";
        private final LocalDate VALID_DOB = LocalDate.of(2000, 1, 1);
        private final String VALID_PHONE = "1234567890";
        private final String VALID_ADDRESS = "123 Main St";
        private final byte[] VALID_PASS_HASH = PasswordUtils.hashPassword("password123");

        void createUser_validRegistrationInput_shouldReturnUser() {
                User user = UserFactory.createUser(
                                VALID_FIRST,
                                VALID_LAST,
                                VALID_DOB,
                                VALID_PHONE,
                                VALID_EMAIL,
                                VALID_ADDRESS,
                                VALID_PASS_HASH);
                assertNotNull(user);
        }

        @Test
        void createUser_validDatabaseInput_shouldReturnUser() {
                User user = UserFactory.createUser(
                                VALID_ID,
                                VALID_FIRST,
                                VALID_LAST,
                                VALID_DOB,
                                VALID_PHONE,
                                VALID_EMAIL,
                                VALID_ADDRESS,
                                VALID_PASS_HASH);
                assertNotNull(user);
        }

        /// --- Tests for registration method ---//
        /// --- Null input tests ---#

        @Test
        void createUser_Registration_throws_whenFirstNameIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(null, VALID_LAST, VALID_DOB, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Registration_throws_whenLastNameIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_FIRST, null, VALID_DOB, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Registration_throws_whenDateOfBirthIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, null, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Registration_throws_whenPhoneNumberIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, VALID_DOB, null, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Registration_throws_whenEmailIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, VALID_DOB, VALID_PHONE, null,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Registration_throws_whenAddressIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, VALID_DOB, VALID_PHONE,
                                                VALID_EMAIL, null, VALID_PASS_HASH));
        }

        // Parameter validation tests ---#

        @Test
        void createUser_invalidFirstName_throwsException() {
                assertThrows(IllegalArgumentException.class,
                                () -> UserFactory.createUser("1234", VALID_LAST, VALID_DOB, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_emptyLastName_throwsException() {
                assertThrows(IllegalArgumentException.class,
                                () -> UserFactory.createUser(VALID_FIRST, "   ", VALID_DOB, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_futureDateOfBirth_throwsException() {
                assertThrows(IllegalArgumentException.class, () -> UserFactory.createUser(VALID_FIRST, VALID_LAST,
                                LocalDate.now().plusDays(1), VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                                VALID_PASS_HASH));
        }

        @Test
        void createUser_underageUser_throwsException() {
                LocalDate dob = LocalDate.now().minusYears(17);
                assertThrows(IllegalArgumentException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, dob, VALID_PHONE, VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_invalidPhoneNumber_throwsException() {
                assertThrows(IllegalArgumentException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, VALID_DOB, "12345", VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_invalidEmailFormat_throwsException() {
                assertThrows(IllegalArgumentException.class,
                                () -> UserFactory.createUser(VALID_FIRST, VALID_LAST, VALID_DOB,
                                                VALID_PHONE, "bad-email", VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        // --- Tests for retrieval method ---#
        // --- Null input tests ---#

        @Test
        void createUser_Retrieval_throws_whenUserIdIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(null, VALID_FIRST, VALID_LAST, VALID_DOB, VALID_PHONE,
                                                VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenFirstNameIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, null, VALID_LAST, VALID_DOB, VALID_PHONE,
                                                VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenLastNameIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, VALID_FIRST, null, VALID_DOB, VALID_PHONE,
                                                VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenDateOfBirthIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, VALID_FIRST, VALID_LAST, null,
                                                VALID_PHONE, VALID_EMAIL, VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenPhoneNumberIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, VALID_FIRST, VALID_LAST, VALID_DOB, null,
                                                VALID_EMAIL,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenEmailIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, VALID_FIRST, VALID_LAST, VALID_DOB, VALID_PHONE,
                                                null,
                                                VALID_ADDRESS,
                                                VALID_PASS_HASH));
        }

        @Test
        void createUser_Retrieval_throws_whenAddressIsNull() {
                assertThrows(NullPointerException.class,
                                () -> UserFactory.createUser(VALID_ID, VALID_FIRST, VALID_LAST, VALID_DOB, VALID_PHONE,
                                                VALID_EMAIL,
                                                null,
                                                VALID_PASS_HASH));
        }

}
