package com.capbank.user_service.core.application.ports.out;

import com.capbank.user_service.infra.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findById(UUID id);
    Optional<UserEntity> findByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}
