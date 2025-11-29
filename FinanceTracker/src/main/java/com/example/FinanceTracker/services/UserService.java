package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.UserDTO;
import com.example.FinanceTracker.entities.UserEntity;
import com.example.FinanceTracker.mappers.UserMapper;
import com.example.FinanceTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDTO register(UserDTO userDTO) {
        UserEntity userEntity =userMapper.toUserEntity(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userEntity.setPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toUserDTO(savedUserEntity);
    }
}
