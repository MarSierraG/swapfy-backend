package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Transaction;
import com.swapfy.backend.models.User;
import com.swapfy.backend.repositories.UserRepository;
import com.swapfy.backend.services.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @Autowired
    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    // Obtener todas las transacciones
    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    // Obtener una transacción por su ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);

        if (transaction.isPresent()) {
            return ResponseEntity.ok(transaction.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Transacción con ID " + id + " no encontrada.");
        }
    }

    // Crear una nueva transacción
    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    // Actualizar una transacción existente
    @PutMapping("/{id}")
    public Transaction updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        return transactionService.updateTransaction(id, transactionDetails);
    }

    // Eliminar una transacción
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTransactionsByUser(@PathVariable Long userId, Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Comprobamos si el usuario autenticado es admin
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        // Recuperamos el email del token JWT
        String email = authentication.getName();

        // Buscamos el usuario en la base de datos
        Optional<User> optionalUser = userRepository.findOptionalByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }
        User authenticatedUser = optionalUser.get();

        // Solo puede acceder si es admin o es el mismo usuario
        if (!isAdmin && !authenticatedUser.getUserId().equals(userId)) {
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        // Obtenemos las transacciones del usuario
        List<Transaction> transactions = transactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(transactions);
    }

}
