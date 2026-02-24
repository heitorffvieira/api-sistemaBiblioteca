package br.com.vieiradev.apiBiblioteca.services;

import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import br.com.vieiradev.apiBiblioteca.exceptions.ResourceNotFoundException;
import br.com.vieiradev.apiBiblioteca.models.Book;
import br.com.vieiradev.apiBiblioteca.models.Client;
import br.com.vieiradev.apiBiblioteca.models.Loan;
import br.com.vieiradev.apiBiblioteca.models.LoanStatus;
import br.com.vieiradev.apiBiblioteca.repositories.BookRepository;
import br.com.vieiradev.apiBiblioteca.repositories.ClientRepository;
import br.com.vieiradev.apiBiblioteca.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ClientRepository clientRepository;

    public List<Loan> getAll() {
        return loanRepository.findAll();
    }

    public Loan getById(Long id) {
        return findLoanOrThrow(id);
    }

    public Loan createLoan(Long bookId, Long clientId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado."));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        if (loanRepository.existsByClientIdAndBookIdAndStatus(clientId, bookId, LoanStatus.EM_ANDAMENTO)) {
            throw new BusinessException("Cliente já possui este livro emprestado.");
        }

        if (book.getAvailableQuantity() <= 0) {
            throw new BusinessException("Livro sem estoque.");
        }

        LocalDate today = LocalDate.now();

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setClient(client);
        loan.setLoanDate(today);
        loan.setReturnDate(today.plusDays(7));
        loan.setStatus(LoanStatus.EM_ANDAMENTO);
        loan.setFineValue(BigDecimal.ZERO);

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        return loanRepository.save(loan);
    }

    public Loan returnLoan(Long id) {

        Loan loan = findLoanOrThrow(id);

        if (loan.getStatus() != LoanStatus.EM_ANDAMENTO) {
            throw new BusinessException("Esse empréstimo já foi finalizado.");
        }

        LocalDate today = LocalDate.now();
        LocalDate expectedReturnDate = loan.getReturnDate();

        long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, today);

        BigDecimal fine = BigDecimal.ZERO;

        if (daysLate > 0) {
            BigDecimal finePerDay = new BigDecimal("2.00");
            fine = finePerDay.multiply(BigDecimal.valueOf(daysLate));
        }

        loan.setStatus(LoanStatus.DEVOLVIDO);
        loan.setFineValue(fine);

        Book book = loan.getBook();
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);

        return loanRepository.save(loan);
    }

    public void delete(Long id) {

        Loan loan = findLoanOrThrow(id);

        if (loan.getStatus() == LoanStatus.EM_ANDAMENTO) {
            throw new BusinessException("Não é possível excluir um empréstimo em andamento.");
        }

        loanRepository.delete(loan);
    }

    private Loan findLoanOrThrow(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado."));
    }
}
