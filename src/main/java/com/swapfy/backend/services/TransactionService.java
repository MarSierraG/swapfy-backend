package com.swapfy.backend.services;

import com.swapfy.backend.models.Transaction;
import com.swapfy.backend.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
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
        return transactionRepository.save(transaction);
    }

    // Actualizar una transacción existente
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
        transaction.setCreditsTransferred(transactionDetails.getCreditsTransferred());
        transaction.setStatus(transactionDetails.getStatus());
        return transactionRepository.save(transaction);
    }

    // Eliminar una transacción
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
