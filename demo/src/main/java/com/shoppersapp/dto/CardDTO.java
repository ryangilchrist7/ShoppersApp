package com.shoppersapp.dto;

import java.time.LocalDate;

public class CardDTO {

    private String longCardNumber;
    private String CVV;
    private LocalDate expiry;

    public CardDTO() {
    }

    public CardDTO(String longCardNumber, String CVV, LocalDate expiry) {
        this.longCardNumber = longCardNumber;
        this.CVV = CVV;
        this.expiry = expiry;
    }

    public String getLongCardNumber() {
        return longCardNumber;
    }

    public void setLongCardNumber(String longCardNumber) {
        this.longCardNumber = longCardNumber;
    }

    public String getCVV() {
        return CVV;
    }

    public void setCVV(String CVV) {
        this.CVV = CVV;
    }

    public LocalDate getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry = expiry;
    }
}
