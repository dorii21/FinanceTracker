package com.example.FinanceTracker.mappers;

import com.example.FinanceTracker.dtos.UserDTO;
import com.example.FinanceTracker.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(UserDTO userDTO);

    UserDTO toUserDTO(UserEntity userEntity);
}
