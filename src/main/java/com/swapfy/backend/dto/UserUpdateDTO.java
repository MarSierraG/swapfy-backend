package com.swapfy.backend.dto;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String name;
    private String email;
    private String location;
    private String biography;
}
