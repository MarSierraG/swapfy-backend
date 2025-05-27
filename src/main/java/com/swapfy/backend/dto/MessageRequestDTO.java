package com.swapfy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record MessageRequestDTO(
        @NotNull Long senderUserId,
        @NotNull Long receiverUserId,
        @NotBlank String content
) {}