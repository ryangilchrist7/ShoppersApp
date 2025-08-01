package com.shoppersapp.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionDTO {

    private Integer transactionId;
    private String accountNumber;
    private String sortCode;
    private BigDecimal amount;
    private String transactionType;
    private BigDecimal startingBalance;
    private BigDecimal closingBalance;
    private Timestamp createdAt;

    // No-arg constructor
    public TransactionDTO() {
    }

    // All-args constructor
    public TransactionDTO(Integer transactionId, String accountNumber, String sortCode, BigDecimal amount,
            String transactionType, BigDecimal startingBalance, BigDecimal closingBalance, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.amount = amount;
        this.transactionType = transactionType;
        this.startingBalance = startingBalance;
        this.closingBalance = closingBalance;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(BigDecimal startingBalance) {
        this.startingBalance = startingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}