package com.swapfy.backend.controllers;

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
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
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


        // Verificamos si hay cambios reales
        boolean sameName = existingUser.getName().equals(userDetails.getName());
        boolean sameLocation = Objects.equals(existingUser.getLocation(), userDetails.getLocation());
        boolean sameBiography = Objects.equals(existingUser.getBiography(), userDetails.getBiography());

        if (sameName && sameLocation && sameBiography) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No se detectaron cambios en los datos del usuario.");
        }

        // Realizamos la actualización
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Buscar usuarios por nombre
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
}
