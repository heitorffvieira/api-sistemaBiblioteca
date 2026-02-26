package br.com.vieiradev.apiBiblioteca.dtos;

import br.com.vieiradev.apiBiblioteca.models.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanResponseDTO(
        Long id,
        Long bookId,
        String bookTitle,
        Long clientId,
        String clientName,
        LocalDate loanDate,
        LocalDate returnDate,
        LoanStatus status,
        BigDecimal fineValue
) {}
