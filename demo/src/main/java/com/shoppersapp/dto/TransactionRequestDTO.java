package com.shoppersapp.dto;

import java.math.BigDecimal;

public class TransactionRequestDTO {
    private String accountNumber;
    private String sortCode;
    private String longCardNumber;
    private String cvv;
    private String transactionType;
    private BigDecimal amount;

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

    public String getLongCardNumber() {
        return longCardNumber;
    }

    public void setLongCardNumber(String longCardNumber) {
        this.longCardNumber = longCardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}