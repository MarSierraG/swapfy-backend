package com.swapfy.backend.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String location;
    private String biography;
    private Integer credits;
}
