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

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = SQLException.class)
public class DepositService {
    private final BankAccountRepository bankAccountRepository;
    private final DepositLogger depositLogger;

    @Autowired
    public DepositService(BankAccountRepository bankAccountRepository, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.depositLogger = new DepositLogger(transactionRepository);
    }

    public void deposit(BankAccountId bankAccountId, BigDecimal amount) throws SQLException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0.");
        }

        BankAccount bankAccount = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + bankAccountId));
        BigDecimal startingBalance = bankAccount.getBalance();
        BigDecimal closingBalance = startingBalance.add(amount);
        bankAccount.setBalance(closingBalance);
        try {
            this.bankAccountRepository.save(bankAccount);
            this.depositLogger.logDeposit(bankAccount, amount, startingBalance, closingBalance);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private class DepositLogger {
        private final TransactionRepository transactionRepository;

        private DepositLogger(TransactionRepository transactionRepository) {
            this.transactionRepository = transactionRepository;
        }

        private void logDeposit(BankAccount bankAccount, BigDecimal amount,
                BigDecimal startingBalance, BigDecimal closingBalance) throws SQLException {
            Transaction transaction = TransactionFactory.createTransaction(null,
                    bankAccount,
                    null,
                    amount,
                    startingBalance,
                    closingBalance,
                    TransactionType.DEPOSIT,
                    null);
            Transaction saved = this.transactionRepository.save(transaction);
            System.out.println("Saved transaction: " + saved);
        }
    }
}
