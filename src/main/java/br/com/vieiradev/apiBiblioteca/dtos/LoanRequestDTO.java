package br.com.vieiradev.apiBiblioteca.dtos;

import jakarta.validation.constraints.NotNull;

public record LoanRequestDTO(

        @NotNull(message = "BookId é obrigatório.")
        Long bookId,

        @NotNull(message = "ClientId é obrigatório.")
        Long clientId

) {}
