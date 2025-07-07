package com.shoppersapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    // Composite foreign key columns to BankAccount
    @Column(name = "account_number", length = 8, nullable = false)
    private String accountNumber;

    @Column(name = "sort_code", length = 8, nullable = false)
    private String sortCode;

    // Composite foreign key columns to DebitCard (nullable if transaction_type !=
    // PURCHASE)
    @Column(name = "long_card_number", length = 16)
    private String longCardNumber;

    @Column(name = "cvv", length = 3)
    private String cvv;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "account_number", referencedColumnName = "account_number", insertable = false, updatable = false),
            @JoinColumn(name = "sort_code", referencedColumnName = "sort_code", insertable = false, updatable = false)
    })
    private BankAccount bankAccount;

    @ManyToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "long_card_number", referencedColumnName = "long_card_number", insertable = false, updatable = false),
            @JoinColumn(name = "cvv", referencedColumnName = "cvv", insertable = false, updatable = false)
    })
    private DebitCard debitCard;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 20, nullable = false)
    private TransactionType transactionType;

    @Column(name = "starting_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal startingBalance;

    @Column(name = "closing_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal closingBalance;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    public Transaction() {
    }

    // Getters and setters for all fields

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

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
        if (bankAccount != null) {
            this.accountNumber = bankAccount.getBankAccountId().getAccountNumber();
            this.sortCode = bankAccount.getBankAccountId().getSortCode();
        } else {
            this.accountNumber = null;
            this.sortCode = null;
        }
    }

    public DebitCard getDebitCard() {
        return debitCard;
    }

    public void setDebitCard(DebitCard debitCard) {
        this.debitCard = debitCard;
        if (debitCard != null) {
            this.longCardNumber = debitCard.getDebitCardId().getLongCardNumber();
            this.cvv = debitCard.getDebitCardId().getCVV();
        } else {
            this.longCardNumber = null;
            this.cvv = null;
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
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