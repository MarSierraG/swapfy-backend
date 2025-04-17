package com.swapfy.backend.repositories;

import com.swapfy.backend.models.User;
import com.swapfy.backend.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método para buscar un usuario por su correo electrónico
    Optional<User> findByEmail(String email);


    List<User> findByNameContainingIgnoreCase(String name);
    Optional<User> findOptionalByEmail(String email);


}

