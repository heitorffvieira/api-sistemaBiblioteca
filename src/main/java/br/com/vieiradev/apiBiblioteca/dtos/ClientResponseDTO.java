package br.com.vieiradev.apiBiblioteca.dtos;

public record ClientResponseDTO(
        Long id,
        String name,
        String email,
        String cpf
) {}
