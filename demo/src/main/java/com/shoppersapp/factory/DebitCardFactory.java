package com.shoppersapp.factory;

import java.time.LocalDate;

import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.model.BankAccount;

public class DebitCardFactory {

    /**
     * Creates a new debit card object.
     */
    public static DebitCard createDebitCard(BankAccount bankAccount, DebitCardId debitCardId,
            LocalDate expirationDate) {
        if (isValidDebitCard(debitCardId.getLongCardNumber(), debitCardId.getCVV(), expirationDate)) {
            return new DebitCard(debitCardId, bankAccount, expirationDate);
        }
        return null;
    }

    /**
     * @return true if the passed parameters are valid
     * @throws NullPointerException if any parameter is null
     */
    private static boolean isValidDebitCard(String longCardNumber, String cvv, LocalDate expirationDate)
            throws NullPointerException {
        if (longCardNumber == null || cvv == null || expirationDate == null) {
            throw new NullPointerException("Missing debit card details");
        }
        return isValidLongCardNumber(longCardNumber) && isValidCvv(cvv) && isValidExpirationDate(expirationDate);
    }

    /**
     * @return true if the parameter is in the correct format
     * @throws IllegalArgumentException if the parameter is not a 16 digit numeric
     *                                  string
     */
    private static boolean isValidLongCardNumber(String longCardNumber) throws IllegalArgumentException {
        if (longCardNumber.matches("\\d{16}")) {
            return true;
        }
        throw new IllegalArgumentException("Invalid long card number (16 digit string)");
    }

    /**
     * @return true if the parameter is in the correct format
     * @throws IllegalArgumentException if the parameter is not a 3 digit numeric
     *                                  string
     */
    private static boolean isValidCvv(String cvv) throws IllegalArgumentException {
        if (cvv.matches("\\d{3}")) {
            return true;
        }
        throw new IllegalArgumentException("CVV not in correct format (3 digit string)");
    }

    /**
     * @return true if the parameter is valid
     * @throws IllegalArgumentException if the parameter is a date in future
     */
    private static boolean isValidExpirationDate(LocalDate expirationDate) {
        if (expirationDate.isAfter(LocalDate.now())) {
            return true;
        }
        throw new IllegalArgumentException("Expiration date invalid (date in the future)");
    }
}
