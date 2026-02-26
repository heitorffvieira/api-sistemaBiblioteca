package br.com.vieiradev.apiBiblioteca.dtos;

public record BookResponseDTO(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publicationYear,
        Integer totalQuantity,
        Integer availableQuantity
) {}
