package com.shoppersapp.repositories;

import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.model.BankAccountId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DebitCardRepository extends JpaRepository<DebitCard, DebitCardId> {
    Optional<DebitCard> findByBankAccount_BankAccountId(BankAccountId bankAccountId);
}