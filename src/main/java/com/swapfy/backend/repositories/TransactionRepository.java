package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser1UserIdOrUser2UserId(Long user1Id, Long user2Id);
}
