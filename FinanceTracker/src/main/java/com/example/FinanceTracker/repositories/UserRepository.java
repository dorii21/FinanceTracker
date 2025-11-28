package com.example.FinanceTracker.repositories;

import com.example.FinanceTracker.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
}
