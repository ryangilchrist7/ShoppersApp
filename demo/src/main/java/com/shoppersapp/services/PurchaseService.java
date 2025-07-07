package com.shoppersapp.services;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.Transaction;
import com.shoppersapp.model.TransactionType;
import com.shoppersapp.factory.TransactionFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;
import com.shoppersapp.repositories.TransactionRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService {
    private final BankAccountRepository bankAccountRepository;
    private final DebitCardRepository debitCardRepository;
    private final PurchaseLogger purchaseLogger;
    private final RewardsHelper rewardsHelper;

    @Autowired
    public PurchaseService(BankAccountRepository bankAccountRepository, DebitCardRepository debitCardRepository,
            TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.debitCardRepository = debitCardRepository;
        this.purchaseLogger = new PurchaseLogger(transactionRepository);
        this.rewardsHelper = new RewardsHelper(bankAccountRepository, transactionRepository);
    }

    public void purchase(DebitCard debitCard, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase amount must be greater than 0.");
        }

        Optional<DebitCard> optionalCard = this.debitCardRepository.findById(
                debitCard.getDebitCardId());
        DebitCard cardOnFile = optionalCard
                .orElseThrow(() -> new IllegalArgumentException("No card with those details exist"));
        BankAccount bankAccount = cardOnFile.getBankAccount();

        BigDecimal startingBalance = bankAccount.getBalance();
        BigDecimal closingBalance = startingBalance.subtract(amount);
        bankAccount.setBalance(closingBalance);
        try {
            this.bankAccountRepository.save(bankAccount);
            purchaseLogger.logPurchase(bankAccount, debitCard, amount,
                    startingBalance, closingBalance);
            rewardsHelper.creditReward(bankAccount, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class PurchaseLogger {
        private final TransactionRepository transactionRepository;

        private PurchaseLogger(TransactionRepository transactionRepository) {
            this.transactionRepository = transactionRepository;
        }

        private void logPurchase(BankAccount bankAccount, DebitCard debitCard, BigDecimal amount,
                BigDecimal startingBalance, BigDecimal closingBalance) throws SQLException {
            Transaction transaction = TransactionFactory.createTransaction(null,
                    bankAccount,
                    debitCard,
                    amount,
                    startingBalance,
                    closingBalance,
                    TransactionType.PURCHASE,
                    null);
            this.transactionRepository.save(transaction);
        }
    }

    private class RewardsHelper {
        private final BigDecimal REWARDS_RATE = new BigDecimal(0.01);
        private final BankAccountRepository bankAccountRepository;
        private final RewardsLogger rewardsLogger;

        private RewardsHelper(BankAccountRepository bankAccountRepository,
                TransactionRepository transactionRepository) {
            this.bankAccountRepository = bankAccountRepository;
            this.rewardsLogger = new RewardsLogger(transactionRepository);
        }

        private void creditReward(BankAccount bankAccount, BigDecimal purchaseAmount) throws SQLException {
            BankAccount accountOnFile = this.bankAccountRepository.findById(bankAccount.getBankAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
            BigDecimal interestAccrued = accountOnFile.getInterestAccrued();
            BigDecimal eligibleReward = purchaseAmount.multiply(REWARDS_RATE);

            BigDecimal reward;
            if (accountOnFile.getInterestAccrued().compareTo(BigDecimal.ZERO) <= 0) {
                reward = BigDecimal.ZERO;
            } else if (accountOnFile.getInterestAccrued().compareTo(eligibleReward) < 0) {
                reward = interestAccrued;
            } else {
                reward = eligibleReward;
            }

            if (reward.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal startingBalance = accountOnFile.getBalance();
                BigDecimal closingBalance = accountOnFile.getBalance().add(reward);
                accountOnFile.setBalance(accountOnFile.getBalance().add(reward));
                accountOnFile.setInterestAccrued(accountOnFile.getInterestAccrued().subtract(reward));
                this.bankAccountRepository.save(accountOnFile);
                rewardsLogger.logReward(accountOnFile, reward, startingBalance, closingBalance);
            }
        }
    }

    private class RewardsLogger {
        private final TransactionRepository transactionRepository;

        private RewardsLogger(TransactionRepository transactionRepository) {
            this.transactionRepository = transactionRepository;
        }

        private void logReward(BankAccount bankAccount, BigDecimal amount,
                BigDecimal startingBalance, BigDecimal closingBalance) throws SQLException {
            Transaction transaction = TransactionFactory.createTransaction(null,
                    bankAccount,
                    null,
                    amount,
                    startingBalance,
                    closingBalance,
                    TransactionType.REWARD,
                    null);
            this.transactionRepository.save(transaction);
        }
    }
}
