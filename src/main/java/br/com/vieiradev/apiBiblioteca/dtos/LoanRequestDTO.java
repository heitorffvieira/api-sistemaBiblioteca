package br.com.vieiradev.apiBiblioteca.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequestDTO {
    private Long bookId;
    private Long clientId;
}
