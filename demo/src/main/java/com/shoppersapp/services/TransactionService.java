package com.shoppersapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppersapp.model.Transaction;
import com.shoppersapp.repositories.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber, String sortCode) {
        return transactionRepository.findAllTransactionsByBankAccountId(accountNumber, sortCode);
    }
}