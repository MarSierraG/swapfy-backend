package com.swapfy.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String location;
    private String biography;
    private Integer credits;
    private List<String> roles;
    private LocalDateTime registrationDate;
}
