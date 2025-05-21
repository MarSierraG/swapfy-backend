package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.UserDTO;
import com.swapfy.backend.dto.UserUpdateDTO;
import com.swapfy.backend.exceptions.ForbiddenException;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.SecurityService;
import com.swapfy.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    // Eliminar un usuario (admin o sí mismo)
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(authUser.getRole().getName());
        boolean isSelf = authUser.getUserId().equals(userId);

        if (!isAdmin && !isSelf) {
            throw new ForbiddenException("No tienes permisos para eliminar este usuario.");
        }

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    // Actualizar usuario (admin o sí mismo)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO userDetails) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(authUser.getRole().getName());
        boolean isSelf = authUser.getUserId().equals(id);

        if (!isAdmin && !isSelf) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permisos para actualizar este usuario.");
        }

        User existingUser = userService.getUserById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        // Actualizamos campos básicos
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setLocation(userDetails.getLocation());
        existingUser.setBiography(userDetails.getBiography());
        existingUser.setCredits(userDetails.getCredits());

        // Rol si viene en el DTO
        if (userDetails.getRole() != null) {
            userService.setUserRole(existingUser, userDetails.getRole());
        }

        User updatedUser = userService.saveUser(existingUser);

        // Convertimos a UserDTO para devolverlo
        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(updatedUser.getUserId());
        responseDTO.setName(updatedUser.getName());
        responseDTO.setEmail(updatedUser.getEmail());
        responseDTO.setLocation(updatedUser.getLocation());
        responseDTO.setBiography(updatedUser.getBiography());
        responseDTO.setCredits(updatedUser.getCredits());
        responseDTO.setRoles(List.of(updatedUser.getRole().getName()));

        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email,
                                                    @RequestParam(required = false) Long excludeId) {
        boolean exists = userService.emailExistsForOtherUser(email, excludeId);
        return ResponseEntity.ok(exists);
    }


    // Buscar usuarios por nombre
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        System.out.println(" getAllUsers() fue llamado");

        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(401).body(null);
        }

        System.out.println("Usuario autenticado: " + authUser.getEmail() + " | Rol: " + authUser.getRole().getName());

        boolean isAdmin = "ADMIN".equalsIgnoreCase(authUser.getRole().getName());

        if (!isAdmin) {
            throw new ForbiddenException("Solo los administradores pueden ver la lista completa de usuarios.");
        }

        List<UserDTO> userDTOs = userService.getAllUsers().stream().map(user -> {
            UserDTO dto = new UserDTO();
            dto.setUserId(user.getUserId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setLocation(user.getLocation());
            dto.setBiography(user.getBiography());
            dto.setCredits(user.getCredits());
            dto.setRoles(List.of(user.getRole().getName()));
            return dto;
        }).toList();

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User authUser = securityService.getAuthenticatedUser();

        if (authUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(authUser.getRole().getName());
        boolean isSelf = authUser.getUserId().equals(id);

        if (!isAdmin && !isSelf) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para ver este usuario.");
        }

        User existingUser = userService.getUserById(id);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        UserDTO responseDTO = new UserDTO();
        responseDTO.setUserId(existingUser.getUserId());
        responseDTO.setName(existingUser.getName());
        responseDTO.setEmail(existingUser.getEmail());
        responseDTO.setLocation(existingUser.getLocation());
        responseDTO.setBiography(existingUser.getBiography());
        responseDTO.setCredits(existingUser.getCredits());
        responseDTO.setRoles(List.of(existingUser.getRole().getName()));

        return ResponseEntity.ok(responseDTO);
    }


}
