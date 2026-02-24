package br.com.vieiradev.apiBiblioteca.repositories;

import br.com.vieiradev.apiBiblioteca.models.Loan;
import br.com.vieiradev.apiBiblioteca.models.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByClientIdAndStatus(Long clientId, LoanStatus status);
    boolean existsByBookId(Long bookId);
    boolean existsByClientIdAndBookIdAndStatus(Long clientId, Long bookId, LoanStatus status);
}
