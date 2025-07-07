package com.shoppersapp.demo.services;

import com.shoppersapp.services.DebitCardService;
import com.shoppersapp.model.BankAccount;
import com.shoppersapp.model.BankAccountId;
import com.shoppersapp.model.DebitCard;
import com.shoppersapp.model.DebitCardId;
import com.shoppersapp.factory.DebitCardFactory;
import com.shoppersapp.repositories.DebitCardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

public class DebitCardServiceTest {

        private DebitCardRepository debitCardRepository;
        private DebitCardService debitCardService;

        @BeforeEach
        void setUp() {
                debitCardRepository = mock(DebitCardRepository.class);
                debitCardService = new DebitCardService(debitCardRepository);
        }

        @Test
        void testGenerateDebitCardDetails_CreatesUniqueCard() {
                BankAccount mockBankAccount = mock(BankAccount.class);
                BankAccountId bankAccountId = new BankAccountId("12345678", "12-34-56");
                when(mockBankAccount.getBankAccountId()).thenReturn(bankAccountId);

                when(debitCardRepository.findById(any())).thenReturn(Optional.empty());

                try (MockedStatic<DebitCardFactory> factoryMock = mockStatic(DebitCardFactory.class)) {
                        DebitCard mockDebitCard = mock(DebitCard.class);

                        factoryMock.when(() -> DebitCardFactory.createDebitCard(
                                        any(BankAccount.class),
                                        any(DebitCardId.class),
                                        eq(LocalDate.of(2030, 1, 1)))).thenReturn(mockDebitCard);

                        DebitCard result = debitCardService.generateDebitCardDetails(mockBankAccount);

                        assertNotNull(result);
                        factoryMock.verify(() -> DebitCardFactory.createDebitCard(
                                        eq(mockBankAccount),
                                        any(DebitCardId.class),
                                        eq(LocalDate.of(2030, 1, 1))));
                }
        }

        @Test
        void testGenerateDebitCardDetails_RetriesOnCollision() {
                BankAccount mockBankAccount = mock(BankAccount.class);
                BankAccountId bankAccountId = new BankAccountId("12345678", "12-34-56");
                when(mockBankAccount.getBankAccountId()).thenReturn(bankAccountId);

                DebitCard existingCard = mock(DebitCard.class);

                when(debitCardRepository.findById(any()))
                                .thenReturn(Optional.empty())
                                .thenReturn(Optional.of(existingCard))
                                .thenReturn(Optional.empty());
        }
}