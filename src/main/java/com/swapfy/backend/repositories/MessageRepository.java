package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Buscar mensajes enviados por un usuario
    List<Message> findBySender_UserId(Long senderId);

    // Buscar mensajes recibidos por un usuario
    List<Message> findByReceiver_UserId(Long receiverId);

    // Conversación entre dos usuarios
    List<Message> findBySender_UserIdAndReceiver_UserIdOrReceiver_UserIdAndSender_UserId(
            Long sender1, Long receiver1, Long sender2, Long receiver2
    );
}
