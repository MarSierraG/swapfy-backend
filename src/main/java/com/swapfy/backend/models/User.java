package com.swapfy.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Email(message = "El email no es válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    private String location;
    private String biography;

    @Min(value = 0, message = "Los créditos no pueden ser negativos")
    private Integer credits;

    private LocalDateTime registrationDate;

    // Campo de rol, por ejemplo "USER" o "ADMIN"
    @NotBlank(message = "El rol es obligatorio")
    private String role;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
}
