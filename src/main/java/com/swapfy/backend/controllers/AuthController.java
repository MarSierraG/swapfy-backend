package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.LoginResponseDTO;
import com.swapfy.backend.dto.UserDTO;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.AuthService;
import com.swapfy.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import com.swapfy.backend.dto.ResetPasswordRequest;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    // Registro
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        User registeredUser = authService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    // Login que devuelve JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User validUser = authService.login(user.getEmail(), user.getPassword());

        if (validUser == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getName(), user.getUserId());


        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(validUser.getUserId());
        userDTO.setName(validUser.getName());
        userDTO.setEmail(validUser.getEmail());
        userDTO.setLocation(validUser.getLocation());
        userDTO.setBiography(validUser.getBiography());
        userDTO.setCredits(validUser.getCredits());
        userDTO.setRegistrationDate(Instant.from(validUser.getRegistrationDate()));

        List<String> roleNames = List.of(validUser.getRole().getName());
        userDTO.setRoles(roleNames);

        userDTO.setRoles(roleNames);

        LoginResponseDTO response = new LoginResponseDTO(token, userDTO);

        return ResponseEntity.ok(response);
    }

    // Resetear contraseña
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        boolean success = authService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } else {
            return ResponseEntity.status(400).body("Código inválido o expirado");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> sendResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        boolean success = authService.sendResetCode(email);

        Map<String, String> response = new HashMap<>();

        if (success) {
            response.put("message", "Código enviado al correo si existe una cuenta con ese email");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No se encontró ninguna cuenta con ese email");
            return ResponseEntity.status(404).body(response);
        }
    }

}
