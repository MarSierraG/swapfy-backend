package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    List<Credit> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    List<Credit> findByUser_UserIdAndAmountLessThan(Long userId, int threshold);

}

