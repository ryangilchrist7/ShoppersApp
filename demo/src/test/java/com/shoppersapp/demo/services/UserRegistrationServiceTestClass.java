package com.shoppersapp.demo.services;

import java.sql.SQLException;
import java.time.LocalDate;

import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.UserFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;
import com.shoppersapp.repositories.UserRepository;
import com.shoppersapp.utils.PasswordUtils;
import com.shoppersapp.repositories.InterestIssueRepository;
import com.shoppersapp.services.BankAccountService;
import com.shoppersapp.services.DebitCardService;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.User;
import com.shoppersapp.model.DebitCard;

public class UserRegistrationServiceTestClass {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private final BankAccountService bankAccountService;
    private final DebitCardService debitCardService;

    public UserRegistrationServiceTestClass(
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            DebitCardRepository debitCardRepository,
            InterestIssueRepository interestIssueRepository,
            BankAccountService bankAccountService,
            DebitCardService debitCardService,
            PasswordUtils passwordUtils) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.debitCardRepository = debitCardRepository;
        this.bankAccountService = bankAccountService;
        this.debitCardService = debitCardService;
    }

    public boolean registerUser(String firstName, LocalDate dateOfBirth, String lastName, String phoneNumber,
            String address, String email, String password)
            throws IdentifierInUseException, IllegalArgumentException, RuntimeException, SQLException {

        if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IdentifierInUseException("A user with that phone number or email address already exists.");
        }

        byte[] hashedPassword = PasswordUtils.hashPassword(password);

        User user = UserFactory.createUser(firstName, lastName, dateOfBirth, phoneNumber, email, address,
                hashedPassword);
        userRepository.save(user);

        User registeredUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with that email not found"));
        BankAccount bankAccount = bankAccountService.generateBankAccountDetails(registeredUser);
        DebitCard debitCard = debitCardService.generateDebitCardDetails(bankAccount);

        bankAccountRepository.save(bankAccount);
        debitCardRepository.save(debitCard);

        return true;
    }
}