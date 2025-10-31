package com.capbank.user_service.core.application.service;

import com.capbank.user_service.core.application.ports.in.*;
import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.infra.client.dto.AuthResponseDTO;
import com.capbank.user_service.infra.dto.*;
import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.mapper.UserMapper;
import com.capbank.user_service.infra.metrics.UserMetrics;
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
    private final UserMetrics userMetrics;

    public UserServiceImpl(UserRepositoryPort repository, PasswordEncoder passwordEncoder, UserMapper mapper,
                           GatewayClientPort gatewayClient,  UserMetrics userMetrics) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.gatewayClient = gatewayClient;
        this.userMetrics = userMetrics;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        final String cpf = normalizeCpf(request.getCpf());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        if (repository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
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
            userMetrics.incrementCreateFailure();
            LOG.error("Failed to create bank account for userId={}, aborting user creation before persistence. Reason: {}", userEntity.getId(), ex.getMessage());
            throw new IllegalStateException("Falha ao criar conta bancária. Cadastro do usuário abortado.", ex);
        }

        UserEntity saved = repository.save(userEntity);
        userMetrics.incrementCreateSuccess();
        LOG.info("UserEntity created id={}, cpfHash={}", saved.getId(), safeHash(cpf));

        return mapper.toResponse(saved);
    }

    @Override
    public UserLoginResponse validate(ValidateUserRequest request) {
        String normalized = normalizeCpf(request.getCpf());

        UserEntity user = repository.findByCpf(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        boolean validPassword = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!validPassword) {
            LOG.warn("Invalid password for cpfHash={}", safeHash(normalized));
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        AuthResponseDTO tokenResponse;
        try {
            tokenResponse = gatewayClient.loginForUser(request.getCpf(), request.getPassword());
        } catch (RuntimeException ex) {
            LOG.error("Failed to generate token for cpfHash={}: {}", safeHash(normalized), ex.getMessage());
            throw new IllegalStateException("Falha ao gerar token. Login do usuário abortado.", ex);
        }

        UserResponse userResponse = mapper.toResponse(user);
        LOG.info("User validated successfully cpfHash={}", safeHash(normalized));
        return new UserLoginResponse(userResponse, tokenResponse);
    }

    @Override
    public UserResponse getByCpf(String cpf) {
        String normalized = normalizeCpf(cpf);
        UserEntity user = repository.findByCpf(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        return mapper.toResponse(user);
    }

    @Override
    public UserResponse update(String cpf, UpdateUserRequest request) {
        String normalized = normalizeCpf(cpf);
        UserEntity user = repository.findByCpf(normalized)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && repository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("E-mail já cadastrado.");
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
            throw new IllegalArgumentException("Usuário não encontrado.");
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
