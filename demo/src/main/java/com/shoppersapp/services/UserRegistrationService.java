package com.shoppersapp.services;

import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppersapp.exceptions.IdentifierInUseException;
import com.shoppersapp.factory.UserFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;
import com.shoppersapp.repositories.InterestIssueRepository;
import com.shoppersapp.repositories.UserRepository;
import com.shoppersapp.utils.PasswordUtils;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.User;
import com.shoppersapp.model.DebitCard;

@Service
@Transactional(rollbackFor = SQLException.class)
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private final BankAccountService bankAccountService;
    private final DebitCardService debitCardService;

    @Autowired
    public UserRegistrationService(
            UserRepository userRepository,
            BankAccountRepository bankAccountRepository,
            DebitCardRepository debitCardRepository,
            InterestIssueRepository interestIssueRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.debitCardRepository = debitCardRepository;
        this.bankAccountService = new BankAccountService(this.bankAccountRepository, interestIssueRepository);
        this.debitCardService = new DebitCardService(this.debitCardRepository);
    }

    /**
     * Attempts to register a user using the given details.
     * 
     * @return true if the user, bank account and debit card were inserted into the
     *         database successfully
     */
    public void registerUser(String firstName, LocalDate dateOfBirth, String lastName, String phoneNumber,
            String address, String email, String password)
            throws IdentifierInUseException, IllegalArgumentException, RuntimeException, SQLException {
        try {
            if (userRepository.existsByEmail(email) || userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new IdentifierInUseException("A user with that phone number or email address already exists.");
            }

            byte[] hashedPassword = PasswordUtils.hashPassword(password);

            User user = UserFactory.createUser(firstName, lastName, dateOfBirth, phoneNumber, email, address,
                    hashedPassword);

            userRepository.save(user);

            User registeredUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found after registration"));

            BankAccount bankAccount = this.bankAccountService.generateBankAccountDetails(registeredUser);

            DebitCard debitCard = this.debitCardService.generateDebitCardDetails(bankAccount);

            this.bankAccountRepository.save(bankAccount);
            this.debitCardRepository.save(debitCard);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
