package com.capbank.user_service.core.application.service;

import com.capbank.user_service.core.application.ports.in.DeleteUserUseCase;
import com.capbank.user_service.core.application.ports.in.GetUserUseCase;
import com.capbank.user_service.core.application.ports.in.RegisterUserUseCase;
import com.capbank.user_service.core.application.ports.in.UpdateUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateUserUseCase;
import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UpdateUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.dto.ValidateUserRequest;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements RegisterUserUseCase, ValidateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final GatewayClientPort gatewayClient;

    public UserServiceImpl(UserRepositoryPort repository, PasswordEncoder passwordEncoder, UserMapper mapper,
                           GatewayClientPort gatewayClient) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.gatewayClient = gatewayClient;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        final String cpf = normalizeCpf(request.getCpf());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (repository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF already registered.");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        UserEntity userEntity = mapper.toEntity(request);
        userEntity.setCpf(cpf);
        userEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userEntity.setStatus(UserEntity.Status.ACTIVE);

        var generatedId = UUID.randomUUID();
        userEntity.setId(generatedId);

        try {
            gatewayClient.createForUser(userEntity.getId(), userEntity.getAccountType());
        } catch (RuntimeException ex) {
            LOG.error("Failed to create bank account for userId={}, aborting user creation before persistence. Reason: {}", userEntity.getId(), ex.getMessage());
            throw new IllegalStateException("Failed to create bank account. User registration aborted.", ex);
        }

        UserEntity saved = repository.save(userEntity);
        LOG.info("UserEntity created id={}, cpfHash={}", saved.getId(), safeHash(cpf));

        return mapper.toResponse(saved);
    }

    @Override
    public boolean validate(ValidateUserRequest request) {
        String cpf = normalizeCpf(request.getCpf());
        return repository.findByCpf(cpf)
                .map(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .orElse(false);
    }

    @Override
    public UserResponse getByCpf(String cpf) {
        String normalized = normalizeCpf(cpf);
        UserEntity user = repository.findByCpf(normalized)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return mapper.toResponse(user);
    }

    @Override
    public UserResponse update(String cpf, UpdateUserRequest request) {
        String normalized = normalizeCpf(cpf);
        UserEntity user = repository.findByCpf(normalized)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && repository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email already registered.");
            }
            user.setEmail(newEmail);
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getAccountType() != null) {
            user.setAccountType(request.getAccountType());
        }

        UserEntity saved = repository.save(user);
        LOG.info("UserEntity updated id={}, cpfHash={}", saved.getId(), safeHash(normalized));
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(String cpf) {
        String normalized = normalizeCpf(cpf);
        boolean exists = repository.findByCpf(normalized).isPresent();
        if (!exists) {
            throw new IllegalArgumentException("User not found.");
        }
        repository.deleteByCpf(normalized);
        LOG.info("UserEntity deleted cpfHash={}", safeHash(normalized));
    }

    private String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D+", "");
    }

    private String safeHash(String value) {
        return Integer.toHexString(value == null ? 0 : value.hashCode());
    }
}
