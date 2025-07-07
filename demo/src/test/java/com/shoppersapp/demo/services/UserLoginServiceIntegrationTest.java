package com.shoppersapp.demo.services;

import com.shoppersapp.ShoppersAppApplication;
import com.shoppersapp.dto.UserLoginDTO;
import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.InterestIssueFactory;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.repositories.*;
import com.shoppersapp.services.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShoppersAppApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserLoginServiceIntegrationTest {
    private Connection connection;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private InterestIssueRepository interestIssueRepository;

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserLoginService userLoginService;

    private final String VALID_FIRST = "Alice";
    private final String VALID_LAST = "Smith";
    private final LocalDate VALID_DOB = LocalDate.of(1995, 1, 1);
    private final String VALID_PHONE = "+1234567890";
    private final String VALID_EMAIL = "testuser@example.com";
    private final String VALID_ADDRESS = "10 Test Street";
    private final String VALID_PASS = "securepassword123";

    @BeforeEach
    public void setUp() throws Exception {
        this.connection = dataSource.getConnection();

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE TABLE transactions, cards, bankaccounts, accounts, interestissues CASCADE");
        }
        InterestIssue interestIssue = InterestIssueFactory.createInterestIssue(BigDecimal.valueOf(1.0));
        this.interestIssueRepository.save(interestIssue);

        userRegistrationService.registerUser(
                VALID_FIRST,
                VALID_DOB,
                VALID_LAST,
                VALID_PHONE,
                VALID_ADDRESS,
                VALID_EMAIL,
                VALID_PASS);
    }

    @Test
    public void testUserLoginSuccessWithEmail() throws SQLException, IdentifierInUseException {
        UserLoginDTO dto = this.userLoginService.login(VALID_EMAIL, VALID_PASS);
        assertNotNull(dto);
    }

    @Test
    public void testUserLoginSuccessWithPhoneNumber() throws SQLException, IdentifierInUseException {
        UserLoginDTO dto = this.userLoginService.login(VALID_PHONE, VALID_PASS);
        assertNotNull(dto);
    }

    @Test
    public void testUserLoginFail_ThrowsSQLException() throws SQLException, IdentifierInUseException {
        UserLoginDTO dto = this.userLoginService.login("+1234567899", VALID_PASS);
        assertNull(dto);
    }
}
