package com.shoppersapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BankAccountId implements Serializable {

    @Column(name = "account_number", length = 8, nullable = false)
    private String accountNumber;

    @Column(name = "sort_code", length = 6, nullable = false)
    private String sortCode;

    public BankAccountId() {
    }

    public BankAccountId(String accountNumber, String sortCode) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getSortCode() {
        return this.sortCode;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BankAccountId))
            return false;
        BankAccountId that = (BankAccountId) o;
        return accountNumber.equals(that.accountNumber) && sortCode.equals(that.sortCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, sortCode);
    }
}