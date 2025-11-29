package com.example.FinanceTracker.mappers;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    TransactionEntity toEntity(TransactionDTO transactionDTO);

    TransactionDTO toDTO(TransactionEntity transactionEntity);

    List<TransactionDTO> toDTOs(List<TransactionEntity> transactionEntity);

    @Mapping(target = "user", ignore = true)
    TransactionEntity updateTransactionFromDTO(TransactionDTO transactionDTO, @MappingTarget TransactionEntity transactionEntity);
}
