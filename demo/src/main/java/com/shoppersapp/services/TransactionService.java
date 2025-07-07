package com.shoppersapp.services;

import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.Transaction;
import com.shoppersapp.repositories.TransactionRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * @return a list of transactions associated with that bankAccountId
     */
    public List<Transaction> getTransactionList(BankAccountId bankAccountId) {
        List<Transaction> transactions = this.transactionRepository
                .findAllTransactionsByBankAccountId(bankAccountId.getAccountNumber(), bankAccountId.getSortCode());

        if (transactions.isEmpty()) {
            throw new IllegalArgumentException("No transactions recorded for that bank account");
        }
        return transactions;
    }
}
