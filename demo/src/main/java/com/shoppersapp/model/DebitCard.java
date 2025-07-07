package com.shoppersapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class DebitCard {

    @EmbeddedId
    private DebitCardId debitCardId;

    @OneToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "account_number", referencedColumnName = "account_number"),
            @JoinColumn(name = "sort_code", referencedColumnName = "sort_code")
    })
    private BankAccount bankAccount;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Deprecated
    public DebitCard() {
    }

    public DebitCard(DebitCardId debitCardId, BankAccount bankAccount, LocalDate expirationDate) {
        this.debitCardId = debitCardId;
        this.bankAccount = bankAccount;
        this.expirationDate = expirationDate;
    }

    public DebitCardId getDebitCardId() {
        return debitCardId;
    }

    public void setDebitCardId(DebitCardId debitCardId) {
        this.debitCardId = debitCardId;
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}