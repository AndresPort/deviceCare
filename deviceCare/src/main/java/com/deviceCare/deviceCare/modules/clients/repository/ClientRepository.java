package com.deviceCare.deviceCare.modules.clients.repository;

import com.deviceCare.deviceCare.modules.clients.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Client> findByDocumentNumberAndDeletedAtIsNull(String documentNumber);

    boolean existsByDocumentNumberAndDeletedAtIsNull(String documentNumber);
}