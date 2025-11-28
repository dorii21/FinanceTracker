package com.example.FinanceTracker.mappers;

import com.example.FinanceTracker.dtos.TransactionDTO;
import com.example.FinanceTracker.entities.TransactionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionEntity toEntity(TransactionDTO transactionDTO);

    TransactionDTO toDTO(TransactionEntity transactionEntity);

    List<TransactionEntity> toEntities(List<TransactionDTO> transactionDTO);

    List<TransactionDTO> toDTOs(List<TransactionEntity> transactionEntity);

}
