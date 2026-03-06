package br.com.vieiradev.apiBiblioteca.repositories;

import br.com.vieiradev.apiBiblioteca.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    List<Client> findByNameContainingIgnoreCase(String name);
}
