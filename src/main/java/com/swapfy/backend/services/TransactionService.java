package com.swapfy.backend.services;

import com.swapfy.backend.models.Transaction;
import com.swapfy.backend.models.User;
import com.swapfy.backend.models.Item;
import com.swapfy.backend.repositories.TransactionRepository;
import com.swapfy.backend.repositories.UserRepository;
import com.swapfy.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    // Obtener todas las transacciones
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Obtener una transacción por su ID
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    // Crear una nueva transacción
    public Transaction createTransaction(Transaction transaction) {
        // Recupera las entidades gestionadas desde la base de datos:
        User user1 = userRepository.findById(transaction.getUser1().getUserId())
                .orElseThrow(() -> new RuntimeException("User1 no encontrado"));
        User user2 = userRepository.findById(transaction.getUser2().getUserId())
                .orElseThrow(() -> new RuntimeException("User2 no encontrado"));
        Item item1 = itemRepository.findById(transaction.getItem1().getItemId())
                .orElseThrow(() -> new RuntimeException("Item1 no encontrado"));
        Item item2 = itemRepository.findById(transaction.getItem2().getItemId())
                .orElseThrow(() -> new RuntimeException("Item2 no encontrado"));

        // Establece las entidades gestionadas en la transacción:
        transaction.setUser1(user1);
        transaction.setUser2(user2);
        transaction.setItem1(item1);
        transaction.setItem2(item2);

        return transactionRepository.save(transaction);
    }

    // Actualizar una transacción existente
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        transaction.setCreditsTransferred(transactionDetails.getCreditsTransferred());
        transaction.setStatus(transactionDetails.getStatus());
        return transactionRepository.save(transaction);
    }

    // Eliminar una transacción
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUser1UserIdOrUser2UserId(userId, userId);
    }

}
