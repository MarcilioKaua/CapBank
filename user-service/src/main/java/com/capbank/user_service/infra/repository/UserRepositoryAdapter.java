package com.capbank.user_service.infra.repository;

import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.infra.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryAdapter extends UserRepositoryPort, JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
