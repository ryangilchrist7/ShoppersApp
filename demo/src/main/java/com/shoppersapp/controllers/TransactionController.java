package com.shoppersapp.controllers;

import com.shoppersapp.dto.TransactionDTO;
import com.shoppersapp.dto.TransactionRequestDTO;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.model.Transaction;
import com.shoppersapp.model.TransactionType;
import com.shoppersapp.repositories.TransactionRepository;
import com.shoppersapp.services.DepositService;
import com.shoppersapp.services.WithdrawalService;
import com.shoppersapp.services.PurchaseService;
import com.shoppersapp.services.TransactionService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private DepositService depositService;

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, String>> handleTransaction(@RequestBody TransactionRequestDTO request) {
        Map<String, String> response = new HashMap<>();

        try {
            TransactionType type = TransactionType.valueOf(request.getTransactionType().toUpperCase());
            BigDecimal amount = request.getAmount();

            switch (type) {
                case DEPOSIT:
                    depositService.deposit(
                            new BankAccountId(request.getAccountNumber(), request.getSortCode()), amount);
                    response.put("message", "Deposit successful.");
                    return ResponseEntity.ok(response);

                case WITHDRAWAL:
                    withdrawalService.withdraw(
                            new BankAccountId(request.getAccountNumber(), request.getSortCode()), amount);
                    response.put("message", "Withdrawal successful.");
                    return ResponseEntity.ok(response);

                case PURCHASE:
                    DebitCardId id = new DebitCardId(request.getLongCardNumber(), request.getCvv());
                    DebitCard debitCard = new DebitCard();
                    debitCard.setDebitCardId(id);
                    purchaseService.purchase(debitCard, amount);
                    response.put("message", "Purchase successful.");
                    return ResponseEntity.ok(response);
                default:
                    response.put("error", "Unsupported transaction type.");
                    return ResponseEntity.badRequest().body(response);
            }

        } catch (IllegalArgumentException e) {
            response.put("error", "Invalid transaction type: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Transaction failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/transactions/{accountNumber}/{sortCode}")
    public ResponseEntity<?> getTransactionsByAccount(
            @PathVariable String accountNumber,
            @PathVariable String sortCode) {

        if (accountNumber == null || accountNumber.isEmpty() ||
                sortCode == null || sortCode.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Account number and sort code must be provided.");
        }

        if (accountNumber.length() != 8) {
            return ResponseEntity.badRequest()
                    .body("Account number is not in the expected format");
        }

        if (sortCode.length() != 6) {
            return ResponseEntity.badRequest()
                    .body("Sort code is not in the expected format");
        }

        List<Transaction> transactions = transactionService.getTransactionsByAccount(
                accountNumber, sortCode);

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No transactions found for this account.");
        }

        List<TransactionDTO> dtos = transactions.stream()
                .map(t -> new TransactionDTO(
                        t.getTransactionId(),
                        t.getAccountNumber(),
                        t.getSortCode(),
                        t.getAmount(),
                        t.getTransactionType().toString(),
                        t.getStartingBalance(),
                        t.getClosingBalance(),
                        t.getCreatedAt()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}