package com.shoppersapp.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppersapp.model.DebitCard;
import com.shoppersapp.dto.AccountDTO;
import com.shoppersapp.dto.CardDTO;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.repositories.BankAccountRepository;
import com.shoppersapp.repositories.DebitCardRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private DebitCardRepository debitCardRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<AccountDTO> getAccountByUserId(@PathVariable Integer userId) {
        BankAccount account = bankAccountRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("There is no bank account associated with that ID"));
        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        AccountDTO dto = new AccountDTO(
                account.getBankAccountId().getAccountNumber(),
                account.getBankAccountId().getSortCode(),
                account.getBalance(),
                account.getInterestAccrued());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{accountNumber}/{sortCode}")
    public ResponseEntity<?> getCardByAccount(@PathVariable String accountNumber,
            @PathVariable String sortCode) {
        BankAccountId bankAccountId = new BankAccountId(accountNumber, sortCode);

        Optional<DebitCard> optionalDebitCard = debitCardRepository.findByBankAccount_BankAccountId(bankAccountId);
        if (optionalDebitCard.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No debit card associated with that bank account");
        }

        DebitCard debitCard = optionalDebitCard.get();

        if (debitCard.getExpirationDate().isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The card has expired and cannot be used");
        }

        CardDTO dto = new CardDTO(
                debitCard.getDebitCardId().getLongCardNumber(),
                debitCard.getDebitCardId().getCVV(),
                debitCard.getExpirationDate());

        return ResponseEntity.ok(dto);
    }
}
