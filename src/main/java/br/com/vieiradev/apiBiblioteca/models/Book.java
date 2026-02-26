package br.com.vieiradev.apiBiblioteca.models;

import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false, updatable = false)
    private String isbn;

    @Column(nullable = false)
    private Integer publicationYear;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    public Book(String title,
                String author,
                String isbn,
                Integer publicationYear,
                Integer totalQuantity) {

        if (totalQuantity == null || totalQuantity < 0) {
            throw new BusinessException("Quantidade total inválida.");
        }

        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateAuthor(String author) {
        this.author = author;
    }

    public void updatePublicationYear(Integer year) {
        this.publicationYear = year;
    }

    public void updateTotalQuantity(Integer newTotalQuantity) {

        if (newTotalQuantity <= 0) {
            throw new BusinessException("Quantidade total deve ser maior que zero.");
        }

        int borrowedBooks = this.totalQuantity - this.availableQuantity;

        if (newTotalQuantity < borrowedBooks) {
            throw new BusinessException(
                    "Nova quantidade não pode ser menor que livros emprestados."
            );
        }

        this.totalQuantity = newTotalQuantity;
        this.availableQuantity = newTotalQuantity - borrowedBooks;
    }

    public void borrow() {
        if (availableQuantity <= 0) {
            throw new BusinessException("Livro sem exemplares disponíveis.");
        }
        availableQuantity--;
    }

    public void returnBook() {
        if (availableQuantity >= totalQuantity) {
            throw new BusinessException("Estado inválido para devolução.");
        }
        availableQuantity++;
    }
}
