package com.swapfy.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditResponseDTO {
    private Long creditId;
    private int amount;
    private String type;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
}
