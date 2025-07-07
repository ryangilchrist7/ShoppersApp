package com.shoppersapp.services;

import java.time.LocalDate;
import java.security.SecureRandom;

import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.factory.DebitCardFactory;
import com.shoppersapp.repositories.DebitCardRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DebitCardService {
    // A constant used to define the expiration date of all newly generated debit
    // cards.
    private static final LocalDate EXPIRATION_DATE = LocalDate.of(2030, 1, 1);
    private static final SecureRandom random = new SecureRandom();
    private final DebitCardRepository debitCardRepository;

    @Autowired
    public DebitCardService(DebitCardRepository debitCardRepository) {
        this.debitCardRepository = debitCardRepository;
    }

    /**
     * Generates the debit card details for the given bank account
     */
    public DebitCard generateDebitCardDetails(BankAccount bankAccount) {
        DebitCardId debitCardId;
        Optional<DebitCard> debitCardOpt;
        do {
            debitCardId = new DebitCardId(generateLongCardNumber(), generateCVV());
            debitCardOpt = this.debitCardRepository.findById(debitCardId);
        } while (debitCardOpt.isPresent());

        return DebitCardFactory.createDebitCard(bankAccount, debitCardId, EXPIRATION_DATE);
    }

    public String generateLongCardNumber() {
        StringBuilder cardNumber = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    public String generateCVV() {
        StringBuilder cvv = new StringBuilder(3);
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        return cvv.toString();
    }
}
