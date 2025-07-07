package com.shoppersapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shoppersapp.model.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.bankAccount.bankAccountId.accountNumber = :accountNumber AND t.bankAccount.bankAccountId.sortCode = :sortCode")
    List<Transaction> findAllTransactionsByBankAccountId(@Param("accountNumber") String accountNumber,
            @Param("sortCode") String sortCode);
}