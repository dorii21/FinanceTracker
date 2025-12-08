package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.ResponseUserDTO;
import com.example.FinanceTracker.dtos.UserDTO;
import com.example.FinanceTracker.entities.UserEntity;
import com.example.FinanceTracker.exceptions.UserAlreadyExistsException;
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

    public ResponseUserDTO register(UserDTO userDTO) throws UserAlreadyExistsException {
        //check if user already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserEntity userEntity = userMapper.toUserEntity(userDTO);

        //only save the encoded password
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userEntity.setPassword(encodedPassword);
        UserEntity savedUserEntity = userRepository.save(userEntity);
        return userMapper.toResponseUserDTO(savedUserEntity);
    }
}
