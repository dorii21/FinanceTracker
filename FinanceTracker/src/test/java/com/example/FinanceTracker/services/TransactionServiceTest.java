package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.UserEntity;
import com.example.FinanceTracker.exceptions.TransactionNotFoundException;
import com.example.FinanceTracker.mappers.TransactionMapper;
import com.example.FinanceTracker.repositories.TransactionRepository;
import com.example.FinanceTracker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private TransactionService transactionService;
    private final String TEST_EMAIL = "test@gmail.com";
    private final Long TEST_ID = 123L;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setEmail(TEST_EMAIL);
        testUser.setId(TEST_ID);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
    }

    @Test
    void testListTransactions_Successful() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        mockEntities.add(new TransactionEntity());
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(new TransactionDTO());

        when(transactionRepository.findByUserId(TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = transactionService.listTransactions();
        verify(transactionRepository).findByUserId(TEST_ID);
        assertEquals(mockDTOs, result);
    }

    @Test
    void testGetTransaction_Successful() {
        TransactionEntity mockentity = new TransactionEntity();
        TransactionDTO mockDTO = new TransactionDTO();
        when(transactionRepository.findByIdAndUserId(mockentity.getId(), TEST_ID)).thenReturn(Optional.of(mockentity));
        when(transactionMapper.toDTO(mockentity)).thenReturn(mockDTO);

        TransactionDTO result = transactionService.getTransaction(mockentity.getId());
        verify(transactionRepository).findByIdAndUserId(mockentity.getId(), TEST_ID);
        assertEquals(mockDTO, result);
    }

    @Test
    void testGetTransaction_Unsuccessful() {
        TransactionEntity mockentity = new TransactionEntity();
        when(transactionRepository.findByIdAndUserId(mockentity.getId(), TEST_ID)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class, () -> transactionService.getTransaction(mockentity.getId()));
    }

}
