package com.capbank.user_service.infra.mapper;

import com.capbank.user_service.infra.entity.UserEntity;
import com.capbank.user_service.infra.dto.RegisterUserRequest;
import com.capbank.user_service.infra.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserEntity toEntity(RegisterUserRequest request);

    UserResponse toResponse(UserEntity userEntity);
}
