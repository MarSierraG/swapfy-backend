package com.swapfy.backend.dto;

import lombok.Data;

@Data
public class CreditDTO {
    private Long userId;
    private int amount;
    private String type;
}
