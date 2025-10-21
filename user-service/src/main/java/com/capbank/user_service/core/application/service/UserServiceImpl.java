package com.capbank.user_service.core.application.service;

import com.capbank.user_service.core.application.ports.in.CreateUserUseCase;
import com.capbank.user_service.core.application.ports.in.GetUserUseCase;
import com.capbank.user_service.core.application.ports.in.ValidateCredentialsUseCase;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.domain.model.User;
import com.capbank.user_service.infra.dto.UserCreateDTO;
import com.capbank.user_service.infra.dto.UserDTO;
import com.capbank.user_service.infra.dto.ValidateCredentialsRequestDTO;
import com.capbank.user_service.infra.exception.UserNotFoundException;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements CreateUserUseCase, GetUserUseCase, ValidateCredentialsUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public UserDTO create(UserCreateDTO dto) {
        User user = mapper.toEntity(dto);
        user.setSenhaHash(passwordEncoder.encode(dto.getSenha()));
        user.setStatus(User.Status.ATIVO);
        return mapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserDTO getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        return mapper.toDTO(user);
    }

    @Override
    public boolean validate(ValidateCredentialsRequestDTO request) {
        return userRepository.findByEmail(request.getEmail())
                .map(u -> passwordEncoder.matches(request.getPassword(), u.getSenhaHash()))
                .orElse(false);
    }
}
