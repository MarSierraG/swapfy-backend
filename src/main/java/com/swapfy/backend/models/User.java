package com.swapfy.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

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
    private String resetCode;

    @Min(value = 0, message = "Los créditos no pueden ser negativos")
    private Integer credits;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private Instant registrationDate;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = Instant.now();
    }
}
