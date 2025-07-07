package com.shoppersapp.services;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.Transaction;
import com.shoppersapp.model.TransactionType;
import com.shoppersapp.factory.TransactionFactory;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = SQLException.class)
public class WithdrawalService {
    private final BankAccountRepository bankAccountRepository;
    private final WithdrawalLogger withdrawalLogger;

    @Autowired
    public WithdrawalService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.withdrawalLogger = new WithdrawalLogger(transactionRepository);
    }

    public void withdraw(BankAccountId bankAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
        }

        BankAccount bankAccount = this.bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Bank account not found"));
        BigDecimal startingBalance = bankAccount.getBalance();
        BigDecimal closingBalance = startingBalance.subtract(amount);
        bankAccount.setBalance(closingBalance);
        try {
            this.bankAccountRepository.save(bankAccount);
            this.withdrawalLogger.logWithdrawal(bankAccount, amount, startingBalance, closingBalance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WithdrawalLogger {
        private final TransactionRepository transactionRepository;

        private WithdrawalLogger(TransactionRepository transactionRepository) {
            this.transactionRepository = transactionRepository;
        }

        private void logWithdrawal(BankAccount bankAccount, BigDecimal amount,
                BigDecimal startingBalance, BigDecimal closingBalance) throws SQLException {
            Transaction transaction = TransactionFactory.createTransaction(null,
                    bankAccount,
                    null,
                    amount,
                    startingBalance,
                    closingBalance,
                    TransactionType.WITHDRAWAL,
                    null);
            this.transactionRepository.save(transaction);
        }
    }
}
