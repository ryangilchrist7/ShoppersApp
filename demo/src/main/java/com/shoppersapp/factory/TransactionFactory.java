package com.shoppersapp.factory;

import com.shoppersapp.model.*;

import java.sql.Timestamp;
import java.math.BigDecimal;

public class TransactionFactory {
    /*
     * Factory for Transaction objects
     */

    /**
     * @param transactionId   This should be null unless in the context of retrieval
     * @param amount          the amount the transaction is changing the account
     *                        balance from
     * @param startingBalance the initial balance of the account at the start of the
     *                        transction
     * @param closingBalance  the closing balance of the account at the end of the
     *                        transaction
     * @param transactionType DEPOSIT, WITHDRAWAL, PURCHASE or REWARD
     * @param createdAt       the time the transaction was inserted into the
     *                        database. This should be null unless in the context of
     *                        retrieval.
     * @return a new transaction
     * @throws NullPointerException     if bankAccountId, amount, startingBalance,
     *                                  closingBalance or transactionType are null
     * @throws NullPointerException     if debitCardId is null for a purchase
     * @throws IllegalArgumentException if the transaction logic is not compatible
     *                                  with the passed transactionType
     */
    public static Transaction createTransaction(
            Integer transactionId,
            BankAccount bankAccount,
            DebitCard debitCard,
            BigDecimal amount,
            BigDecimal startingBalance,
            BigDecimal closingBalance,
            TransactionType transactionType,
            Timestamp createdAt) {
        if (bankAccount == null) {
            throw new NullPointerException("bankAccountId must not be null.");
        }

        if (transactionType == null) {
            throw new NullPointerException("transactionType must not be null.");
        }

        if (amount == null || startingBalance == null || closingBalance == null) {
            throw new NullPointerException("Transaction details are not complete.");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be greater than zero.");
        }

        if (transactionType == TransactionType.PURCHASE && debitCard == null) {
            throw new NullPointerException("debitCardId must not be null for purchase transactions.");
        }

        if (transactionType != TransactionType.PURCHASE && debitCard != null) {
            throw new IllegalArgumentException("debitCardId must be null for non-purchase transactions.");
        }

        if ((transactionType == TransactionType.DEPOSIT || transactionType == TransactionType.REWARD) &&
                closingBalance.compareTo(startingBalance) <= 0) {
            throw new IllegalArgumentException("Transaction logic is not valid" +
                    "Transaction is a deposit or reward and leaves the account with less than the starting balance");
        }

        if ((transactionType == TransactionType.PURCHASE || transactionType == TransactionType.WITHDRAWAL) &&
                closingBalance.compareTo(startingBalance) >= 0) {
            throw new IllegalArgumentException("Transaction logic is not valid" +
                    " Transaction is a purchase or withdrawal and leaves the account more than the starting balance");
        }

        Transaction transaction = new Transaction();
        transaction.setBankAccount(bankAccount);
        transaction.setDebitCard(debitCard);
        transaction.setAmount(amount);
        transaction.setStartingBalance(startingBalance);
        transaction.setClosingBalance(closingBalance);
        transaction.setTransactionType(transactionType);
        if (createdAt == null) {
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        } else {
            transaction.setCreatedAt(createdAt);
        }
        return transaction;
    }
}
