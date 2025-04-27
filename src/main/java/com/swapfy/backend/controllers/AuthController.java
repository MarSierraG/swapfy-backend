package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.LoginResponseDTO;
import com.swapfy.backend.dto.UserDTO;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.AuthService;
import com.swapfy.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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

        // Si no hay errores, registra el usuario
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

        String token = jwtUtil.generateToken(validUser.getEmail(), validUser.getName());


        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(validUser.getUserId());
        userDTO.setName(validUser.getName());
        userDTO.setEmail(validUser.getEmail());
        userDTO.setLocation(validUser.getLocation());
        userDTO.setBiography(validUser.getBiography());
        userDTO.setCredits(validUser.getCredits());

        LoginResponseDTO response = new LoginResponseDTO(token, userDTO);

        return ResponseEntity.ok(response);
    }
}
