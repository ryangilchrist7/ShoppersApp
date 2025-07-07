package com.shoppersapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DebitCardId implements Serializable {

    @Column(name = "long_card_number", length = 16, nullable = false)
    private String longCardNumber;

    @Column(name = "cvv", length = 3, nullable = false)
    private String cvv;

    public DebitCardId() {
    }

    public DebitCardId(String longCardNumber, String cvv) {
        this.longCardNumber = longCardNumber;
        this.cvv = cvv;
    }

    // Getters and setters
    public String getLongCardNumber() {
        return longCardNumber;
    }

    public void setLongCardNumber(String longCardNumber) {
        this.longCardNumber = longCardNumber;
    }

    public String getCVV() {
        return cvv;
    }

    public void setCVV(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DebitCardId))
            return false;
        DebitCardId that = (DebitCardId) o;
        return Objects.equals(longCardNumber, that.longCardNumber) &&
                Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longCardNumber, cvv);
    }
}