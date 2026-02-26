package br.com.vieiradev.apiBiblioteca.services;

import br.com.vieiradev.apiBiblioteca.dtos.LoanResponseDTO;
import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import br.com.vieiradev.apiBiblioteca.exceptions.ResourceNotFoundException;
import br.com.vieiradev.apiBiblioteca.models.*;
import br.com.vieiradev.apiBiblioteca.repositories.BookRepository;
import br.com.vieiradev.apiBiblioteca.repositories.ClientRepository;
import br.com.vieiradev.apiBiblioteca.repositories.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       ClientRepository clientRepository) {

        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.clientRepository = clientRepository;
    }

    public List<LoanResponseDTO> getAll() {
        return loanRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public LoanResponseDTO getById(Long id) {
        Loan loan = findLoanOrThrow(id);
        return toResponseDTO(loan);
    }

    public LoanResponseDTO createLoan(Long bookId, Long clientId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado."));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado."));

        if (loanRepository.existsByClientIdAndBookIdAndStatus(
                clientId, bookId, LoanStatus.EM_ANDAMENTO)) {
            throw new BusinessException("Cliente já possui este livro emprestado.");
        }

        book.borrow();

        LocalDate today = LocalDate.now();

        Loan loan = new Loan(
                book,
                client,
                today,
                today.plusDays(7)
        );

        loanRepository.save(loan);

        return toResponseDTO(loan);
    }

    public LoanResponseDTO returnLoan(Long id) {

        Loan loan = findLoanOrThrow(id);

        LocalDate today = LocalDate.now();
        long daysLate = ChronoUnit.DAYS.between(loan.getReturnDate(), today);

        BigDecimal fine = BigDecimal.ZERO;

        if (daysLate > 0) {
            BigDecimal finePerDay = new BigDecimal("2.00");
            fine = finePerDay.multiply(BigDecimal.valueOf(daysLate));
        }

        loan.finalizeLoan(fine);
        loan.getBook().returnBook();

        return toResponseDTO(loan);
    }

    public void delete(Long id) {

        Loan loan = findLoanOrThrow(id);

        if (loan.getStatus() == LoanStatus.EM_ANDAMENTO) {
            throw new BusinessException(
                    "Não é possível excluir um empréstimo em andamento."
            );
        }

        loanRepository.delete(loan);
    }

    private Loan findLoanOrThrow(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado."));
    }

    private LoanResponseDTO toResponseDTO(Loan loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getClient().getId(),
                loan.getClient().getName(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getFineValue()
        );
    }
}
