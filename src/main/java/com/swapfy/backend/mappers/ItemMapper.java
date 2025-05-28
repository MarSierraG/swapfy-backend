package com.swapfy.backend.mappers;

import com.swapfy.backend.dto.ItemRequestDTO;
import com.swapfy.backend.dto.ItemResponseDTO;
import com.swapfy.backend.models.Item;
import com.swapfy.backend.models.Tag;
import com.swapfy.backend.models.User;
import com.swapfy.backend.dto.TagDTO;


import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    // Crear un Item a partir de un DTO y usuario
    public static Item toEntity(ItemRequestDTO dto, List<Tag> tags, User user) {
        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setCreditValue(dto.getCreditValue());
        item.setStatus(dto.getStatus());
        item.setType(dto.getType());
        item.setTags(tags);
        item.setUser(user);
        item.setImageUrl(dto.getImageUrl());
        return item;
    }

    // Convertir un Item a un DTO de respuesta
    public static ItemResponseDTO toDTO(Item item) {
        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setItemId(item.getItemId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setCreditValue(item.getCreditValue());
        dto.setUserEmail(item.getUser().getEmail());
        dto.setStatus(item.getStatus());
        dto.setType(item.getType());
        dto.setPublicationDate(item.getPublicationDate());
        dto.setUserName(item.getUser().getName());
        dto.setImageUrl(item.getImageUrl());
        dto.setUserId(item.getUser().getUserId());
        dto.setTagNames(item.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
        dto.setTags(item.getTags().stream()
                .map(tag -> new TagDTO(tag.getTagId(), tag.getName()))
                .collect(Collectors.toList()));

        return dto;
    }
}
