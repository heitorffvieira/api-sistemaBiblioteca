package br.com.vieiradev.apiBiblioteca.services;

import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import br.com.vieiradev.apiBiblioteca.exceptions.ResourceNotFoundException;
import br.com.vieiradev.apiBiblioteca.models.Book;
import br.com.vieiradev.apiBiblioteca.repositories.BookRepository;
import br.com.vieiradev.apiBiblioteca.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(Long id) {
        return findBookOrThrow(id);
    }

    @Transactional
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public void delete(Long id) {

        Book book = findBookOrThrow(id);

        if (loanRepository.existsByBookId(id)) {
            throw new BusinessException("Não é possível excluir livro com empréstimos vinculados.");
        }

        bookRepository.delete(book);
    }

    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado."));
    }

}
