package com.swapfy.backend.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ItemResponseDTO {

    private Long itemId;
    private String title;
    private String description;
    private Integer creditValue;
    private String status;
    private String type;
    private Instant publicationDate;

    private String userName;
    private List<String> tagNames;
    private List<TagDTO> tags;

    private String imageUrl;
    private Long userId;
    private String userEmail;
}
