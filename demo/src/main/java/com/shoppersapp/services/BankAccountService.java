package com.shoppersapp.services;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.InterestIssue;
import com.shoppersapp.model.User;
import com.shoppersapp.factory.BankAccountFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.InterestIssueRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {
    // Constant for the sort code used for all bank accounts.
    private static final String SORT_CODE = "000000";
    private static final BigDecimal INITIAL_BALANCE = new BigDecimal(0);
    private static final BigDecimal INITIAL_INTEREST_ACCRUED = new BigDecimal(0);
    private final BankAccountRepository bankAccountRepository;
    private final InterestIssueRepository interestIssueRepository;

    @Autowired
    public BankAccountService(BankAccountRepository bankAccountRepository,
            InterestIssueRepository interestIssueRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.interestIssueRepository = interestIssueRepository;
    }

    /**
     * Generates bank account details of a newly registered user and inserts it into
     * the database.
     * 
     * @param userId The ID of the newly registered user
     * @return The generated bank account details
     */
    public BankAccount generateBankAccountDetails(User user) throws SQLException {
        String accountNumber;
        String sortCode = SORT_CODE;
        boolean isDuplicate = false;

        // Repeatedly generates a new account number until a unique one is found.
        do {
            accountNumber = generateAccountNumber();
            BankAccountId bankAccountId = new BankAccountId(accountNumber, sortCode);
            Optional<BankAccount> existingAccountOpt = bankAccountRepository.findById(bankAccountId);
            isDuplicate = existingAccountOpt.isPresent();
        } while (isDuplicate);

        BankAccountId bankAccountId = new BankAccountId(accountNumber, sortCode);

        InterestIssue latestIssue = interestIssueRepository.findLatestInterestIssue();

        if (latestIssue == null) {
            throw new IllegalStateException("No interest issue found in the database.");
        }

        BankAccount bankAccount = BankAccountFactory.createBankAccount(bankAccountId, user,
                latestIssue,
                INITIAL_BALANCE, INITIAL_INTEREST_ACCRUED);

        return bankAccount;
    }

    /**
     * Randomly generates an 8 digit numeric string
     * 
     * @return the generated account number
     */
    public String generateAccountNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(100000000));
    }

    /**
     * 
     */
    public void accrueInterestForAllAccounts() throws SQLException {
        List<BankAccount> accounts = this.bankAccountRepository.findAll();

        for (BankAccount account : accounts) {
            BigDecimal interest = InterestHelper.calculateInterest(account.getBalance());
            account.setInterestAccrued(account.getInterestAccrued().add(interest));
            this.bankAccountRepository.save(account);
        }
    }

    public static class InterestHelper {
        private static final BigDecimal INTEREST_RATE = new BigDecimal(0.05); // 5% interest rate

        public static BigDecimal calculateInterest(BigDecimal balance) {
            return balance.multiply(INTEREST_RATE);
        }
    }
}
