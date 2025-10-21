package com.capbank.user_service.infra.mapper;


import com.capbank.user_service.core.domain.model.User;
import com.capbank.user_service.infra.dto.UserCreateDTO;
import com.capbank.user_service.infra.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "senhaHash", ignore = true)
    User toEntity(UserCreateDTO dto);

    UserDTO toDTO(User user);
}

