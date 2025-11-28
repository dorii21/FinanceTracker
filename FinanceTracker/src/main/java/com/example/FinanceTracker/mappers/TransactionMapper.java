package com.example.FinanceTracker.mappers;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionEntity toEntity(TransactionDTO transactionDTO);

    TransactionDTO toDTO(TransactionEntity transactionEntity);

    List<TransactionEntity> toEntities(List<TransactionDTO> transactionDTO);

    List<TransactionDTO> toDTOs(List<TransactionEntity> transactionEntity);

    TransactionEntity updateTransactionFromDTO(TransactionDTO transactionDTO, @MappingTarget TransactionEntity transactionEntity);
}
