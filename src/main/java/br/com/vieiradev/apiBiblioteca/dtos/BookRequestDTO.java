package br.com.vieiradev.apiBiblioteca.dtos;

import jakarta.validation.constraints.*;

public record BookRequestDTO(

        @NotBlank(message = "Título é obrigatório")
        String title,

        @NotBlank(message = "Autor é obrigatório")
        String author,

        @NotBlank(message = "ISBN é obrigatório")
        String isbn,

        @NotNull(message = "Ano de publicação é obrigatório")
        @Min(value = 1000, message = "Ano inválido")
        Integer publicationYear,

        @NotNull(message = "Quantidade total é obrigatória")
        @Positive
        Integer totalQuantity

) {}
