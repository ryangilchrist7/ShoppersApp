package com.shoppersapp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interestissues")
public class InterestIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_issue_id", updatable = false, nullable = false)
    private Integer interestIssueId;

    @Column(name = "interest_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Deprecated
    public InterestIssue() {
        // For JPA
    }

    /**
     * Constructor used when creating new InterestIssue before DB generation of ID.
     */
    public InterestIssue(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * Constructor for when loading existing InterestIssue with known ID.
     */
    public InterestIssue(Integer interestIssueId, BigDecimal interestRate) {
        this.interestIssueId = interestIssueId;
        this.interestRate = interestRate;
    }

    public Integer getInterestIssueId() {
        return interestIssueId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}