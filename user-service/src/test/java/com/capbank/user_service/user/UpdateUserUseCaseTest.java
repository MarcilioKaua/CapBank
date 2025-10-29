package com.capbank.user_service.user;

import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.application.service.UserServiceImpl;
import com.capbank.user_service.infra.dto.UpdateUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private UserMapper mapper;

    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("Deve atualizar usuário com sucesso quando dados são válidos")
    void shouldUpdateUserSuccessfully() {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("old@test.com");
        userEntity.setFullName("Old Name");
        userEntity.setCpf("12345678910");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@test.com");
        request.setFullName("New Name");

        when(userRepository.findByCpf(anyString())).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponse(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity saved = invocation.getArgument(0);
            return new UserResponse(
                    saved.getId(),
                    saved.getFullName(),
                    saved.getCpf(),
                    saved.getEmail(),
                    saved.getPhone(),
                    saved.getBirthDate(),
                    saved.getAccountType(),
                    "ACTIVE"
            );
        });

        // Act
        var response = userService.update("12345678910", request);

        // Assert
        assertThat(response.getEmail()).isEqualTo("new@test.com");
        assertThat(response.getFullName()).isEqualTo("New Name");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo email já estiver registrado")
    void shouldThrowWhenEmailAlreadyRegistered() {
        UserEntity userEntity = new UserEntity();
        userEntity.setCpf("12345678910");
        userEntity.setEmail("old@test.com");

        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@test.com");

        when(userRepository.findByCpf("12345678910")).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.update("12345678910", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email already registered.");

        verify(userRepository, never()).save(any());
    }
}
