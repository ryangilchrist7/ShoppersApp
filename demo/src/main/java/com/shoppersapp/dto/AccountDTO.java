package com.shoppersapp.dto;

import java.math.BigDecimal;

public class AccountDTO {
    private String accountNumber;
    private String sortCode;
    private BigDecimal balance;

    public AccountDTO() {
    }

    public AccountDTO(String accountNumber, String sortCode, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.balance = balance;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}