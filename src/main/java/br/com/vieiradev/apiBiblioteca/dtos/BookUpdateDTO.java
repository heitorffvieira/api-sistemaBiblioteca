package br.com.vieiradev.apiBiblioteca.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record BookUpdateDTO(

        String title,
        String author,

        @Min(value = 1000, message = "Ano inválido")
        Integer publicationYear,

        @Positive(message = "Quantidade deve ser maior que zero")
        Integer totalQuantity

) {}
