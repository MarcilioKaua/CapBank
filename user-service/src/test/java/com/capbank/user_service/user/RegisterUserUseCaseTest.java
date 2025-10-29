package com.capbank.user_service.user;

import com.capbank.user_service.core.application.ports.out.GatewayClientPort;
import com.capbank.user_service.core.application.ports.out.UserRepositoryPort;
import com.capbank.user_service.core.application.service.UserServiceImpl;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserUseCaseTest {

    @Mock private UserRepositoryPort userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper mapper;
    @Mock private GatewayClientPort gatewayClient;

    @InjectMocks private UserServiceImpl userService;

    private RegisterUserRequest userRequest;
    private UserEntity userEntity;

    @BeforeEach
    void setup() {
        userRequest = new RegisterUserRequest();
        userRequest.setCpf("123.456.789-10");
        userRequest.setEmail("user@test.com");
        userRequest.setPassword("1234");
        userRequest.setConfirmPassword("1234");
        userRequest.setFullName("User Test");
        userRequest.setPhone("81999999999");
        userRequest.setBirthDate(LocalDate.of(1995, 1, 1));
        userRequest.setAccountType("CURRENT");

        userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setCpf("12345678910");
        userEntity.setEmail("user@test.com");
        userEntity.setPasswordHash("encoded");
        userEntity.setFullName("User Test");
        userEntity.setPhone("81999999999");
        userEntity.setBirthDate(LocalDate.of(1995, 1, 1));
        userEntity.setAccountType("CURRENT");
    }

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(mapper.toEntity(any())).thenReturn(userEntity);
        when(userRepository.save(any())).thenReturn(userEntity);
        when(mapper.toResponse(any())).thenReturn(
                new UserResponse(
                        userEntity.getId(),
                        userEntity.getFullName(),
                        userEntity.getCpf(),
                        userEntity.getEmail(),
                        userEntity.getPhone(),
                        userEntity.getBirthDate(),
                        userEntity.getAccountType(),
                        "ACTIVE"
                )
        );

        doNothing().when(gatewayClient).createForUser(any(), any());

        var response = userService.register(userRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("user@test.com");
        verify(userRepository).save(any());
        verify(gatewayClient).createForUser(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha e confirmação de senha forem diferentes")
    void shouldThrowWhenPasswordsDoNotMatch() {
        userRequest.setConfirmPassword("wrong");

        assertThatThrownBy(() -> userService.register(userRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Passwords do not match");
    }
}
