package com.swapfy.backend.repositories;

import com.swapfy.backend.models.Message;
import com.swapfy.backend.projections.UnreadCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    List<Message> findBySenderUserIdAndReceiverUserIdAndIsReadFalse(Long senderId, Long receiverId);

    @Query("SELECT m.sender.userId AS senderId, COUNT(m) AS count " +
            "FROM Message m " +
            "WHERE m.receiver.userId = :receiverId AND m.isRead = false " +
            "GROUP BY m.sender.userId")
    List<UnreadCountProjection> countUnreadMessagesGroupedBySender(@Param("receiverId") Long receiverId);

}
