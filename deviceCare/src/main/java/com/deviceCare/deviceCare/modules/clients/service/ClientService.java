package com.deviceCare.deviceCare.modules.clients.service;

import com.deviceCare.deviceCare.common.exception.BusinessException;
import com.deviceCare.deviceCare.modules.clients.dto.ClientRequest;
import com.deviceCare.deviceCare.modules.clients.dto.ClientResponse;
import com.deviceCare.deviceCare.modules.clients.model.Client;
import com.deviceCare.deviceCare.modules.clients.repository.ClientRepository;
import com.deviceCare.deviceCare.modules.users.model.User;
import com.deviceCare.deviceCare.modules.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByDocumentNumberAndDeletedAtIsNull(
                request.getDocumentNumber())) {
            throw BusinessException.conflict("El documento ya está registrado");
        }

        Client client = new Client();
        mapRequestToClient(request, client);

        return toResponse(clientRepository.save(client));
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .filter(client -> client.getDeletedAt() == null)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClientResponse findById(UUID id) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        return toResponse(client);
    }

    public ClientResponse findByDocumentNumber(String documentNumber) {
        Client client = clientRepository
                .findByDocumentNumberAndDeletedAtIsNull(documentNumber)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        return toResponse(client);
    }

    @Transactional
    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));

        if (!client.getDocumentNumber().equals(request.getDocumentNumber()) &&
                clientRepository.existsByDocumentNumberAndDeletedAtIsNull(
                        request.getDocumentNumber())) {
            throw BusinessException.conflict("El documento ya está registrado");
        }

        mapRequestToClient(request, client);
        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public void delete(UUID id) {
        Client client = clientRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.notFound("Cliente no encontrado"));
        client.setDeletedAt(OffsetDateTime.now());
        clientRepository.save(client);
    }

    private void mapRequestToClient(ClientRequest request, Client client) {
        client.setDocumentNumber(request.getDocumentNumber());
        client.setAddress(request.getAddress());

        if (request.getUserId() != null) {
            User user = userRepository.findByIdAndDeletedAtIsNull(request.getUserId())
                    .orElseThrow(() -> BusinessException.notFound("Usuario no encontrado"));
            client.setUser(user);
        }
    }

    private ClientResponse toResponse(Client client) {
        ClientResponse response = new ClientResponse();
        response.setId(client.getId());
        response.setDocumentNumber(client.getDocumentNumber());
        response.setAddress(client.getAddress());
        response.setCreatedAt(client.getCreatedAt());

        if (client.getUser() != null) {
            response.setFirstName(client.getUser().getFirstName());
            response.setLastName(client.getUser().getLastName());
            response.setEmail(client.getUser().getEmail());
            response.setPhone(client.getUser().getPhone());
            response.setUserId(client.getUser().getId());
        }

        return response;
    }
}