package com.swapfy.backend.repositories;

import com.swapfy.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Método para buscar un usuario por su correo electrónico
    User findByEmail(String email);
}