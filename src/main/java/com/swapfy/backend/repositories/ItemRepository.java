package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Buscar artículos basados en el estado
    List<Item> findByStatus(String status);

}
