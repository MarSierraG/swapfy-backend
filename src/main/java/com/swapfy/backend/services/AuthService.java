package com.swapfy.backend.services;

import com.swapfy.backend.models.Role;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.RoleRepository;
import com.swapfy.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User registerUser(@Valid User user) {
        // Validación de campos vacíos o nulos
        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getName() == null || user.getName().isEmpty()) {
            throw new RuntimeException("Email, nombre y contraseña son requeridos");
        }

        // Validar si el email tiene formato válido
        if (!isValidEmail(user.getEmail())) {
            throw new RuntimeException("El email no es válido");
        }

        // Verificar si el email ya está registrado
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

      //Validar contraseña fuerte
        if (!isPasswordStrong(user.getPassword())) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, un número, una letra y un símbolo.");
         }

        // Setear contraseña, créditos y fecha de registro
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCredits(100);
        user.setRegistrationDate(Instant.now());


        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"));
        user.setRole(userRole);

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        // Validación de campos vacíos o nulos
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new RuntimeException("Email y contraseña son requeridos");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return user;


    }


    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }


    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
                password.matches(".*\\d.*") &&
                password.matches(".*[a-zA-Z].*") &&
                password.matches(".*[^a-zA-Z0-9].*");
    }


    public boolean resetPassword(String email, String code, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email.trim().toLowerCase());

        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (!code.equals(user.getResetCode())) {
            System.out.println("Código incorrecto");
            System.out.println("Código introducido: " + code);
            System.out.println("Código guardado: " + user.getResetCode());
            return false;
        }

        // Actualizar contraseña
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetCode(null);

        userRepository.save(user);
        return true;
    }


    public boolean sendResetCode(String email) {
        System.out.println(" Buscando usuario con email: " + email);
        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email.trim());

        if (optionalUser.isEmpty()) {
            System.out.println(" Usuario no encontrado");
            return false;
        }



        User user = optionalUser.get();

        // Generar código aleatorio de 6 dígitos
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        System.out.println(" Usuario encontrado. Código generado: " + code);

        user.setResetCode(code);

        try {
            userRepository.save(user);
            System.out.println(" Código guardado correctamente");
        } catch (Exception e) {
            System.out.println(" Error al guardar el código: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Enviar correo real ✉️
        emailService.sendResetCode(email, code);

        return true;
    }



}
