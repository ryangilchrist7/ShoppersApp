package com.shoppersapp.repositories;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, BankAccountId> {
    Optional<BankAccount> findByUser_UserId(Integer userId);
}