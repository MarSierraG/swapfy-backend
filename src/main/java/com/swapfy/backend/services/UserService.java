package com.swapfy.backend.services;

import com.swapfy.backend.models.Role;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.RoleRepository;
import com.swapfy.backend.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public User updateUser(Long userId, User newData) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        existingUser.setName(newData.getName());
        existingUser.setLocation(newData.getLocation());
        existingUser.setBiography(newData.getBiography());

        return userRepository.save(existingUser);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAllWithRolesOrderedByUserId();
    }

    public void setUserRole(User user, String roleName) {
        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido: " + roleName));
        user.setRole(role);
    }

    public boolean emailExistsForOtherUser(String email, Long excludeId) {
        Optional<User> userWithEmail = userRepository.findByEmail(email);
        return userWithEmail.isPresent() && !userWithEmail.get().getUserId().equals(excludeId);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
