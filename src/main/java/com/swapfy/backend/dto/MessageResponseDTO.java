package com.swapfy.backend.dto;

import java.time.Instant;

public record MessageResponseDTO(
        Long messageId,
        Long senderUserId,
        Long receiverUserId,
        String content,
        Instant timestamp
) {}
