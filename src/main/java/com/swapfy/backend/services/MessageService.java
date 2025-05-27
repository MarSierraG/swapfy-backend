package com.swapfy.backend.services;

import com.swapfy.backend.models.Message;
import com.swapfy.backend.models.User;
import com.swapfy.backend.projections.UnreadCountProjection;
import com.swapfy.backend.repositories.MessageRepository;
import com.swapfy.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBySender(Long senderId) {
        return messageRepository.findBySender_UserId(senderId);
    }

    public List<Message> getMessagesByReceiver(Long receiverId) {
        return messageRepository.findByReceiver_UserId(receiverId);
    }

    public List<Message> getConversation(Long user1, Long user2) {
        return messageRepository.findBySender_UserIdAndReceiver_UserIdOrReceiver_UserIdAndSender_UserId(
                user1, user2, user1, user2
        );
    }

    @Autowired
    private UserRepository userRepository;

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElseThrow();
        User receiver = userRepository.findById(receiverId).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("No puedes enviarte mensajes a ti mism@.");
        }

        return messageRepository.save(message);
    }

    public void markMessagesAsRead(Long senderId, Long receiverId) {
        List<Message> unreadMessages = messageRepository.findBySenderUserIdAndReceiverUserIdAndIsReadFalse(senderId, receiverId);
        for (Message message : unreadMessages) {
            message.setRead(true);
        }
        messageRepository.saveAll(unreadMessages);
    }

    public Map<Long, Long> getUnreadMessageSummary(Long receiverId) {
        List<UnreadCountProjection> results = messageRepository.countUnreadMessagesGroupedBySender(receiverId);
        Map<Long, Long> summary = new HashMap<>();

        for (UnreadCountProjection row : results) {
            summary.put(row.getSenderId(), row.getCount());
        }

        return summary;
    }


    public int countUniqueConversationUsers(Long userId) {
        Set<Long> uniqueUserIds = new HashSet<>();

        List<Message> sent = messageRepository.findBySenderUserId(userId);
        List<Message> received = messageRepository.findByReceiverUserId(userId);

        for (Message msg : sent) {
            uniqueUserIds.add(msg.getReceiver().getUserId());
        }

        for (Message msg : received) {
            uniqueUserIds.add(msg.getSender().getUserId());
        }

        // Opcional: eliminarse a sí mismo si está en la lista
        uniqueUserIds.remove(userId);

        return uniqueUserIds.size();
    }

    public void deleteConversation(Long user1, Long user2) {
        List<Message> messages = messageRepository.findBySender_UserIdAndReceiver_UserIdOrReceiver_UserIdAndSender_UserId(
                user1, user2, user1, user2
        );
        messageRepository.deleteAll(messages);
    }


}
