package br.com.vieiradev.apiBiblioteca.services;

import br.com.vieiradev.apiBiblioteca.dtos.BookRequestDTO;
import br.com.vieiradev.apiBiblioteca.dtos.BookResponseDTO;
import br.com.vieiradev.apiBiblioteca.dtos.BookUpdateDTO;
import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import br.com.vieiradev.apiBiblioteca.exceptions.ResourceNotFoundException;
import br.com.vieiradev.apiBiblioteca.models.Book;
import br.com.vieiradev.apiBiblioteca.models.LoanStatus;
import br.com.vieiradev.apiBiblioteca.repositories.BookRepository;
import br.com.vieiradev.apiBiblioteca.repositories.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public List<BookResponseDTO> getAll() {
        return bookRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BookResponseDTO getById(Long id) {
        Book book = findBookOrThrow(id);
        return toResponseDTO(book);
    }

    public BookResponseDTO create(BookRequestDTO dto) {

        if (bookRepository.existsByIsbn(dto.isbn())) {
            throw new BusinessException("Já existe um livro com esse ISBN.");
        }

        Book book = new Book(
                dto.title(),
                dto.author(),
                dto.isbn(),
                dto.publicationYear(),
                dto.totalQuantity()
        );

        Book savedBook = bookRepository.save(book);
        return toResponseDTO(savedBook);
    }

    public BookResponseDTO update(Long id, BookUpdateDTO dto) {

        Book book = findBookOrThrow(id);

        if (dto.title() != null) {
            book.updateTitle(dto.title());
        }

        if (dto.author() != null) {
            book.updateAuthor(dto.author());
        }

        if (dto.publicationYear() != null) {
            book.updatePublicationYear(dto.publicationYear());
        }

        if (dto.totalQuantity() != null) {
            book.updateTotalQuantity(dto.totalQuantity());
        }

        return toResponseDTO(book);
    }

    public void delete(Long id) {

        Book book = findBookOrThrow(id);

        if (loanRepository.existsByBookIdAndStatus(id, LoanStatus.EM_ANDAMENTO)) {
            throw new BusinessException(
                    "Não é possível excluir livro com empréstimos em andamento."
            );
        }

        bookRepository.delete(book);
    }

    public List<BookResponseDTO> searchBooks(String title) {

        if (title == null || title.isBlank()) {
            return List.of();
        }

        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);

        return books.stream()
                .map(book -> new BookResponseDTO(
                        book.getId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getPublicationYear(),
                        book.getTotalQuantity(),
                        book.getAvailableQuantity()
                ))
                .toList();
    }

    private BookResponseDTO toResponseDTO(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getTotalQuantity(),
                book.getAvailableQuantity()
        );
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado."));
    }

}
