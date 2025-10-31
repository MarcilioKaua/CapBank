package com.capbank.user_service.user;

import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.application.service.UserServiceImpl;
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
public class GetUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Deve retornar usuário quando buscar por CPF existente")
    void shouldReturnUserByCpf() {
        UserEntity entity = new UserEntity();
        entity.setCpf("12345678910");
        entity.setEmail("user@test.com");

        when(userRepository.findByCpf("12345678910")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(
                new UserResponse(
                        entity.getId(),
                        entity.getFullName(),
                        entity.getCpf(),
                        entity.getEmail(),
                        entity.getPhone(),
                        entity.getBirthDate(),
                        entity.getAccountType(),
                        "ACTIVE"
                )
        );

        var result = userService.getByCpf("12345678910");
        assertThat(result.getEmail()).isEqualTo("user@test.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado por CPF")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByCpf("999"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}

