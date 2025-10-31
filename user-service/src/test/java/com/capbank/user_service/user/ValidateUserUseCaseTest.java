package com.capbank.user_service.user;

import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.application.service.UserServiceImpl;
import com.capbank.user_service.infra.client.dto.AuthResponseDTO;
import com.capbank.user_service.infra.dto.UserLoginResponse;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.dto.ValidateUserRequest;
import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private GatewayClientPort gatewayClient;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Deve validar o usuário com sucesso quando CPF e senha estão corretos")
    void shouldValidateUserSuccessfully() {
        UserEntity entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setCpf("12345678910");
        entity.setPasswordHash("encoded");
        entity.setFullName("João da Silva");
        entity.setEmail("joao@email.com");

        when(userRepository.findByCpf("12345678910")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);

        AuthResponseDTO tokenResponse = new AuthResponseDTO(
                "access-token",
                3600L,
                "refresh-token",
                7200L
        );
        when(gatewayClient.loginForUser("12345678910", "1234")).thenReturn(tokenResponse);

        UserResponse userResponse = new UserResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getBirthDate(),
                entity.getAccountType(),
                "ACTIVE"
        );
        when(userMapper.toResponse(entity)).thenReturn(userResponse);

        ValidateUserRequest request = new ValidateUserRequest();
        request.setCpf("12345678910");
        request.setPassword("1234");

        UserLoginResponse response = userService.validate(request);

        assertThat(response).isNotNull();
        assertThat(response.user()).isNotNull();
        assertThat(response.user().getCpf()).isEqualTo("12345678910");
        assertThat(response.token()).isNotNull();
        assertThat(response.token().getAccessToken()).isEqualTo("access-token");

        verify(userRepository).findByCpf("12345678910");
        verify(passwordEncoder).matches("1234", "encoded");
        verify(gatewayClient).loginForUser("12345678910", "1234");
        verify(userMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não é encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByCpf("000")).thenReturn(Optional.empty());

        ValidateUserRequest request = new ValidateUserRequest();
        request.setCpf("000");
        request.setPassword("pass");

        assertThatThrownBy(() -> userService.validate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuário não encontrado");

        verify(userRepository).findByCpf("000");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha está incorreta")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {
        UserEntity entity = new UserEntity();
        entity.setCpf("12345678910");
        entity.setPasswordHash("encoded");

        when(userRepository.findByCpf("12345678910")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        ValidateUserRequest request = new ValidateUserRequest();
        request.setCpf("12345678910");
        request.setPassword("wrong");

        assertThatThrownBy(() -> userService.validate(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Credenciais inválidas");

        verify(userRepository).findByCpf("12345678910");
        verify(passwordEncoder).matches("wrong", "encoded");
    }
}