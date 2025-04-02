package com.swapfy.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;  // Importar esta clase
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoderApp() {
        return new BCryptPasswordEncoder();  // Usamos BCrypt para encriptar las contraseñas
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/auth/**") // Deshabilita CSRF solo para los endpoints de autenticación
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Permitir acceso público a los endpoints de /api/auth/**
                        .anyRequest().authenticated()  // Requiere autenticación para otros endpoints
                )
                .httpBasic(withDefaults());  // Configura la autenticación básica

        return http.build();
    }
}
