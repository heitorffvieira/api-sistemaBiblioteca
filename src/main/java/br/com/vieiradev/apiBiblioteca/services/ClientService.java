package br.com.vieiradev.apiBiblioteca.services;

import br.com.vieiradev.apiBiblioteca.dtos.ClientRequestDTO;
import br.com.vieiradev.apiBiblioteca.dtos.ClientResponseDTO;
import br.com.vieiradev.apiBiblioteca.exceptions.BusinessException;
import br.com.vieiradev.apiBiblioteca.exceptions.ResourceNotFoundException;
import br.com.vieiradev.apiBiblioteca.models.Client;
import br.com.vieiradev.apiBiblioteca.models.LoanStatus;
import br.com.vieiradev.apiBiblioteca.repositories.ClientRepository;
import br.com.vieiradev.apiBiblioteca.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final LoanRepository loanRepository;

    public ClientService(ClientRepository clientRepository, LoanRepository loanRepository) {
        this.clientRepository = clientRepository;
        this.loanRepository = loanRepository;
    }

    public ClientResponseDTO save(ClientRequestDTO dto) {

        if (clientRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado.");
        }

        if (clientRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }

        Client client = toEntity(dto);

        Client saved = clientRepository.save(client);

        return toResponseDTO(saved);
    }

    public List<ClientResponseDTO> getAll() {
        return clientRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ClientResponseDTO getById(Long id) {
        Client client = findClientOrThrow(id);
        return toResponseDTO(client);
    }

    public ClientResponseDTO update(Long id, ClientRequestDTO dto) {

        Client client = findClientOrThrow(id);

        if (!client.getEmail().equals(dto.email()) &&
                clientRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Email já cadastrado.");
        }

        if (!client.getCpf().equals(dto.cpf()) &&
                clientRepository.existsByCpf(dto.cpf())) {
            throw new BusinessException("CPF já cadastrado.");
        }

        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setCpf(dto.cpf());

        Client updated = clientRepository.save(client);

        return toResponseDTO(updated);
    }

    public void delete(Long id) {

        Client client = findClientOrThrow(id);

        if (loanRepository.existsByClientIdAndStatus(id, LoanStatus.EM_ANDAMENTO)) {
            throw new BusinessException("Cliente possui empréstimos ativos.");
        }

        clientRepository.delete(client);
    }

    private Client findClientOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente não encontrado."));
    }

    private Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        client.setName(dto.name());
        client.setEmail(dto.email());
        client.setCpf(dto.cpf());
        return client;
    }

    private ClientResponseDTO toResponseDTO(Client client) {
        return new ClientResponseDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getCpf()
        );
    }
}
