package com.swapfy.backend.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        Long messageId,
        Long senderUserId,
        Long receiverUserId,
        String content,
        LocalDateTime timestamp
) {}
