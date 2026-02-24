package br.com.vieiradev.apiBiblioteca.repositories;

import br.com.vieiradev.apiBiblioteca.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
