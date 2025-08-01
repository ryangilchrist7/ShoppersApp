package com.shoppersapp.controllers;

import com.shoppersapp.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/accrue-interest")
    public ResponseEntity<String> accrueInterestForAllAccounts() {
        try {
            bankAccountService.accrueInterestForAllAccounts();
            return ResponseEntity.ok("Interest accrued for all accounts successfully.");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Failed to accrue interest: " + e.getMessage());
        }
    }
}