package com.capbank.user_service.core.application.service;

import com.capbank.user_service.core.application.ports.in.RegisterUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateUserUseCase;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.domain.model.User;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.dto.ValidateUserRequest;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements RegisterUserUseCase, ValidateUserUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepositoryPort repository, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
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

        User user = mapper.toEntity(request);
        user.setCpf(cpf);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(User.Status.ACTIVE);

        User saved = repository.save(user);
        LOG.info("User created id={}, cpfHash={}", saved.getId(), safeHash(cpf));

        return mapper.toResponse(saved);
    }

    @Override
    public boolean validate(ValidateUserRequest request) {
        String cpf = normalizeCpf(request.getCpf());
        return repository.findByCpf(cpf)
                .map(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .orElse(false);
    }

    private String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D+", "");
    }

    private String safeHash(String value) {
        return Integer.toHexString(value == null ? 0 : value.hashCode());
    }
}
