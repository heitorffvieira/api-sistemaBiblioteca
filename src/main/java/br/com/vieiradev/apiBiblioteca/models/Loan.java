package br.com.vieiradev.apiBiblioteca.models;

import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private LocalDate loanDate;

    @Column(nullable = false)
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    private BigDecimal fineValue;

    public Loan(Book book, Client client, LocalDate loanDate, LocalDate returnDate) {
        this.book = book;
        this.client = client;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.status = LoanStatus.EM_ANDAMENTO;
        this.fineValue = BigDecimal.ZERO;
    }

    public void finalizeLoan(BigDecimal fine) {

        if (this.status != LoanStatus.EM_ANDAMENTO) {
            throw new BusinessException("Empréstimo já finalizado.");
        }

        this.status = LoanStatus.DEVOLVIDO;
        this.fineValue = fine;
    }
}
