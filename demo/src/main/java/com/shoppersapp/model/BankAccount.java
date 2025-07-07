package com.shoppersapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bankaccounts")
public class BankAccount {

    @EmbeddedId
    private BankAccountId bankAccountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_issue_id", nullable = false)
    private InterestIssue interestIssue;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "interest_accrued", precision = 15, scale = 2, nullable = false)
    private BigDecimal interestAccrued;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Deprecated
    public BankAccount() {
    }

    public BankAccount(BankAccountId bankAccountId, User user, InterestIssue interestIssue,
            BigDecimal balance, BigDecimal interestAccrued) {
        this.bankAccountId = bankAccountId;
        this.user = user;
        this.interestIssue = interestIssue;
        this.balance = balance;
        this.interestAccrued = interestAccrued;
    }

    public BankAccountId getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(BankAccountId bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public InterestIssue getInterestIssue() {
        return interestIssue;
    }

    public void setInterestIssue(InterestIssue interestIssue) {
        this.interestIssue = interestIssue;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInterestAccrued() {
        return interestAccrued;
    }

    public void setInterestAccrued(BigDecimal interestAccrued) {
        this.interestAccrued = interestAccrued;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}