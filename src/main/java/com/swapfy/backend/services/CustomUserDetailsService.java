package com.swapfy.backend.services;

import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())  // Usamos el email para la autenticación
                .password(user.getPassword())  // Usamos la contraseña encriptada
                .roles("USER")  // Puedes agregar roles según necesites
                .build();
    }
}
