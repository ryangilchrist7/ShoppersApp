package com.shoppersapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppersapp.dto.AccountDTO;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.repositories.BankAccountRepository;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private BankAccountRepository bankAccountRepository;

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
                account.getBalance());
        return ResponseEntity.ok(dto);
    }
}
