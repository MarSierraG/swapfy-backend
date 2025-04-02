package com.swapfy.backend.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // Contraseña encriptada

    @Column(length = 100)
    private String location;

    @Column(length = 100)
    private String biography;

    @Column(nullable = false)
    private Integer credits = 0; // Créditos iniciales en 0

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    // Constructor vacío (necesario para JPA)
    public User() {
        this.registrationDate = LocalDateTime.now(); // Fecha de registro automática
    }
}