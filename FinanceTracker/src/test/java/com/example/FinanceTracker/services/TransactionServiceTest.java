package com.example.FinanceTracker.services;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.Category;
import com.example.FinanceTracker.entities.TransactionEntity;
import com.example.FinanceTracker.entities.TransactionType;
import com.example.FinanceTracker.entities.UserEntity;
import com.example.FinanceTracker.exceptions.TransactionNotFoundException;
import com.example.FinanceTracker.mappers.TransactionMapper;
import com.example.FinanceTracker.repositories.TransactionRepository;
import com.example.FinanceTracker.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {
    private TransactionRepository transactionRepository;
    private TransactionMapper transactionMapper;
    private UserRepository userRepository;

    private SecurityContext securityContext;
    private Authentication authentication;

    private TransactionService underTest;
    private final String TEST_EMAIL = "test@gmail.com";
    private final Long TEST_ID = 123L;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionMapper = Mockito.mock(TransactionMapper.class);
        userRepository = Mockito.mock(UserRepository.class);
        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        underTest = new TransactionService(transactionRepository, transactionMapper, userRepository);
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

        List<TransactionDTO> result = underTest.listTransactions();
        verify(transactionRepository).findByUserId(TEST_ID);
        assertEquals(mockDTOs, result);
    }

    @Test
    void testGetTransaction_Successful() {
        TransactionEntity mockEntity = new TransactionEntity();
        TransactionDTO mockDTO = new TransactionDTO();
        when(transactionRepository.findByIdAndUserId(mockEntity.getId(), TEST_ID)).thenReturn(Optional.of(mockEntity));
        when(transactionMapper.toDTO(mockEntity)).thenReturn(mockDTO);

        TransactionDTO result = underTest.getTransaction(mockEntity.getId());
        verify(transactionRepository).findByIdAndUserId(mockEntity.getId(), TEST_ID);
        assertEquals(mockDTO, result);
    }

    @Test
    void testGetTransaction_Unsuccessful() {
        TransactionEntity mockEntity = new TransactionEntity();
        when(transactionRepository.findByIdAndUserId(mockEntity.getId(), TEST_ID)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFoundException.class, () -> underTest.getTransaction(mockEntity.getId()));
    }

    @Test
    void testCreateTransaction_Successful() {
        TransactionDTO input = new TransactionDTO();
        TransactionEntity toSave = new TransactionEntity();
        TransactionEntity saved = new TransactionEntity();
        TransactionDTO expected = new TransactionDTO();
        input.setType(TransactionType.EXPENSE);
        input.setAmount(2000L);
        input.setCategory(Category.CLOTHING);
        toSave.setType(TransactionType.EXPENSE);
        toSave.setAmount(2000L);
        toSave.setCategory(Category.CLOTHING);
        saved.setType(TransactionType.EXPENSE);
        saved.setAmount(2000L);
        saved.setCategory(Category.CLOTHING);
        saved.setUser(testUser);
        expected.setType(TransactionType.EXPENSE);
        expected.setAmount(2000L);
        expected.setCategory(Category.CLOTHING);

        when(transactionMapper.toEntity(input)).thenReturn(toSave);
        when(transactionRepository.save(toSave)).thenReturn(saved);
        when(transactionMapper.toDTO(saved)).thenReturn(expected);

        TransactionDTO result = underTest.createTransaction(input);
        verify(transactionRepository).save(toSave);
        assertEquals(expected, result);
        assertEquals(testUser, toSave.getUser());
        assertEquals(Category.CLOTHING, toSave.getCategory());
    }

    @Test
    void testCreateTransaction_SetCategoryToNull() {
        TransactionDTO input = new TransactionDTO();
        TransactionEntity toSave = new TransactionEntity();
        TransactionEntity saved = new TransactionEntity();
        TransactionDTO expected = new TransactionDTO();
        input.setType(TransactionType.INCOME);
        input.setAmount(2000L);
        input.setCategory(Category.CLOTHING);
        toSave.setType(TransactionType.INCOME);
        toSave.setAmount(2000L);
        toSave.setCategory(Category.CLOTHING);
        saved.setType(TransactionType.INCOME);
        saved.setAmount(2000L);
        saved.setCategory(null);
        saved.setUser(testUser);
        expected.setType(TransactionType.INCOME);
        expected.setAmount(2000L);
        expected.setCategory(null);

        when(transactionMapper.toEntity(input)).thenReturn(toSave);
        when(transactionRepository.save(toSave)).thenReturn(saved);
        when(transactionMapper.toDTO(saved)).thenReturn(expected);

        TransactionDTO result = underTest.createTransaction(input);
        verify(transactionRepository).save(toSave);
        assertEquals(expected, result);
        assertEquals(testUser, toSave.getUser());
        assertEquals(null, toSave.getCategory());
    }

    @Test
    void testModifyTransaction_Successful() {
        TransactionDTO input = new TransactionDTO();
        input.setId(1L);
        TransactionEntity toUpdate = new TransactionEntity();
        toUpdate.setId(1L);
        TransactionEntity updated = new TransactionEntity();
        updated.setId(1L);
        TransactionEntity saved = new TransactionEntity();
        saved.setId(1L);
        TransactionDTO expected = new TransactionDTO();
        expected.setId(1L);

        when(transactionRepository.findByIdAndUserId(input.getId(), TEST_ID)).thenReturn(Optional.of(toUpdate));
        when(transactionMapper.updateTransactionFromDTO(input, toUpdate)).thenReturn(updated);
        when(transactionRepository.save(toUpdate)).thenReturn(saved);
        when(transactionMapper.toDTO(saved)).thenReturn(expected);

        TransactionDTO result = underTest.modifyTransaction(1L, input);
        verify(transactionRepository).findByIdAndUserId(input.getId(), TEST_ID);
        verify(transactionRepository).save(toUpdate);
        verify(transactionMapper).updateTransactionFromDTO(input, toUpdate);
        verify(transactionMapper).toDTO(toUpdate);
        assertEquals(expected, result);
    }

    @Test
    void testModifyTransaction_Failed() {
        TransactionDTO input = new TransactionDTO();
        input.setId(1L);

        when(transactionRepository.findByIdAndUserId(input.getId(), TEST_ID)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> underTest.modifyTransaction(1L, input));
    }

    @Test
    void testDeleteTransaction_Successful() {
        TransactionEntity toDelete = new TransactionEntity();
        toDelete.setId(1L);
        when(transactionRepository.findByIdAndUserId(toDelete.getId(), TEST_ID)).thenReturn(Optional.of(toDelete));

        underTest.deleteTransaction(toDelete.getId());

        verify(transactionRepository).findByIdAndUserId(toDelete.getId(), TEST_ID);
        verify(transactionRepository).deleteById(toDelete.getId());
    }

    @Test
    void testDeleteTransaction_Failed() {
        TransactionEntity toDelete = new TransactionEntity();

        when(transactionRepository.findByIdAndUserId(toDelete.getId(), TEST_ID)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> underTest.deleteTransaction(toDelete.getId()));
    }

    @Test
    void filterByCategoryTest() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        TransactionEntity transactionEntity = new TransactionEntity();
        mockEntities.add(transactionEntity);
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(transactionDTO);

        when(transactionRepository.findByCategoryAndUserId(transactionDTO.getCategory(), TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = underTest.filterByCategory(transactionDTO.getCategory());
        verify(transactionRepository).findByCategoryAndUserId(transactionEntity.getCategory(), TEST_ID);
        verify(transactionMapper).toDTOs(mockEntities);
        assertEquals(mockDTOs, result);
    }

    @Test
    void filterByAmountBetweenTest() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        TransactionEntity transactionEntity = new TransactionEntity();
        mockEntities.add(transactionEntity);
        transactionEntity.setAmount(2000L);
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(transactionDTO);

        when(transactionRepository.findByAmountBetweenAndUserId(transactionEntity.getAmount(), transactionEntity.getAmount() + 1L, TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = underTest.filterByAmountBetween(transactionEntity.getAmount(), transactionEntity.getAmount() + 1L);
        verify(transactionRepository).findByAmountBetweenAndUserId(transactionEntity.getAmount(), transactionEntity.getAmount() + 1L, TEST_ID);
        verify(transactionMapper).toDTOs(mockEntities);
        assertEquals(mockDTOs, result);
    }

    @Test
    void filterByDateBetweenTest() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        TransactionEntity transactionEntity = new TransactionEntity();
        mockEntities.add(transactionEntity);
        transactionEntity.setDate(LocalDate.of(2025, 12, 1));
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(transactionDTO);

        when(transactionRepository.findByDateBetweenAndUserId(transactionEntity.getDate(), LocalDate.now(), TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = underTest.filterByDateBetween(transactionEntity.getDate(), LocalDate.now());
        verify(transactionRepository).findByDateBetweenAndUserId(transactionEntity.getDate(), LocalDate.now(), TEST_ID);
        verify(transactionMapper).toDTOs(mockEntities);
        assertEquals(mockDTOs, result);
    }

    @Test
    void getExpensesTest() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        TransactionEntity transactionEntity = new TransactionEntity();
        mockEntities.add(transactionEntity);
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(transactionDTO);

        when(transactionRepository.findByTypeAndUserId(TransactionType.EXPENSE, TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = underTest.getExpenses();
        verify(transactionRepository).findByTypeAndUserId(TransactionType.EXPENSE, TEST_ID);
        verify(transactionMapper).toDTOs(mockEntities);
        assertEquals(mockDTOs, result);
    }

    @Test
    void getIncomeTest() {
        List<TransactionEntity> mockEntities = new ArrayList<TransactionEntity>();
        TransactionEntity transactionEntity = new TransactionEntity();
        mockEntities.add(transactionEntity);
        TransactionDTO transactionDTO = new TransactionDTO();
        List<TransactionDTO> mockDTOs = new ArrayList<TransactionDTO>();
        mockDTOs.add(transactionDTO);

        when(transactionRepository.findByTypeAndUserId(TransactionType.INCOME, TEST_ID)).thenReturn(mockEntities);
        when(transactionMapper.toDTOs(mockEntities)).thenReturn(mockDTOs);

        List<TransactionDTO> result = underTest.getIncome();
        verify(transactionRepository).findByTypeAndUserId(TransactionType.INCOME, TEST_ID);
        verify(transactionMapper).toDTOs(mockEntities);
        assertEquals(mockDTOs, result);
    }

    @Test
    void CSVContentTest() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(TransactionType.EXPENSE);
        transactionDTO.setDate(LocalDate.of(2025, 12, 1));
        transactionDTO.setAmount(2000L);
        transactionDTO.setCategory(Category.GROCERIES);
        transactionDTO.setComment("kenyer,tojas");
        List<TransactionDTO> list = List.of(transactionDTO);
        String expected = "Type,Amount,Date,Category,Comment\n" + "EXPENSE,2000,2025-12-01,GROCERIES,\"kenyer,tojas\"\n";
        ByteArrayResource resource = underTest.CSVcontent(list);
        String result = new String(resource.getByteArray(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    @Test
    void CSVContentTest_specialComment() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(TransactionType.EXPENSE);
        transactionDTO.setDate(LocalDate.of(2025, 12, 1));
        transactionDTO.setAmount(2000L);
        transactionDTO.setCategory(Category.GROCERIES);
        transactionDTO.setComment("kenyer,tojas");
        List<TransactionDTO> list = List.of(transactionDTO);
        String expected = "Type,Amount,Date,Category,Comment\n" + "EXPENSE,2000,2025-12-01,GROCERIES,\"kenyer,tojas\"\n";
        ByteArrayResource resource = underTest.CSVcontent(list);
        String result = new String(resource.getByteArray(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    @Test
    void CSVContentTest_specialComment2() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(TransactionType.EXPENSE);
        transactionDTO.setDate(LocalDate.of(2025, 12, 1));
        transactionDTO.setAmount(2000L);
        transactionDTO.setCategory(Category.GROCERIES);
        transactionDTO.setComment("kenyer\rtojas");
        List<TransactionDTO> list = List.of(transactionDTO);
        String expected = "Type,Amount,Date,Category,Comment\n" + "EXPENSE,2000,2025-12-01,GROCERIES,\"kenyer\rtojas\"\n";
        ByteArrayResource resource = underTest.CSVcontent(list);
        String result = new String(resource.getByteArray(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    @Test
    void CSVContentTest_specialComment3() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(TransactionType.EXPENSE);
        transactionDTO.setDate(LocalDate.of(2025, 12, 1));
        transactionDTO.setAmount(2000L);
        transactionDTO.setCategory(Category.GROCERIES);
        transactionDTO.setComment("kenyer\ntojas");
        List<TransactionDTO> list = List.of(transactionDTO);
        String expected = "Type,Amount,Date,Category,Comment\n" + "EXPENSE,2000,2025-12-01,GROCERIES,\"kenyer\ntojas\"\n";
        ByteArrayResource resource = underTest.CSVcontent(list);
        String result = new String(resource.getByteArray(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }

    @Test
    void CSVContentTest_nullValues() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setType(null);
        transactionDTO.setDate(null);
        transactionDTO.setAmount(2000L);
        transactionDTO.setCategory(null);
        transactionDTO.setComment(null);
        List<TransactionDTO> list = List.of(transactionDTO);
        String expected = "Type,Amount,Date,Category,Comment\n" + ",2000,,,\n";
        ByteArrayResource resource = underTest.CSVcontent(list);
        String result = new String(resource.getByteArray(), StandardCharsets.UTF_8);
        assertEquals(expected, result);
    }
}
