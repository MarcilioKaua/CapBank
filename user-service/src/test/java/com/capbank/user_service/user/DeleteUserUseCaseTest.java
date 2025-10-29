package com.capbank.user_service.user;

import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.application.service.UserServiceImpl;
import com.capbank.user_service.infra.entity.UserEntity;
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
class DeleteUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;

    @InjectMocks private UserServiceImpl userService;

    @Test
    @DisplayName("Deve deletar usuário com sucesso quando CPF existe")
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findByCpf(anyString())).thenReturn(Optional.of(new UserEntity()));
        doNothing().when(userRepository).deleteByCpf(anyString());

        userService.delete("12345678910");

        verify(userRepository).deleteByCpf("12345678910");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar deletar um usuário que não existe")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByCpf(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete("000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }
}
