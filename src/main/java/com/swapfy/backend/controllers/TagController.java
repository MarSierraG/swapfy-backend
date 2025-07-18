package com.swapfy.backend.controllers;

import com.swapfy.backend.dto.TagDTO;
import com.swapfy.backend.exceptions.TagInUseException;
import com.swapfy.backend.models.Tag;
import com.swapfy.backend.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService ) {
        this.tagService = tagService;
    }

    // Obtener todas las etiquetas
    @GetMapping
    public List<TagDTO> getAllTags() {
        return tagService.getAllTags().stream()
                .map(tag -> new TagDTO(tag.getTagId(), tag.getName()))
                .toList();
    }
    // Obtener una etiqueta por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        Optional<Tag> tag = tagService.getTagById(id);
        return tag.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear una nueva etiqueta (solo para ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.status(201).body(createdTag);
    }

    // Actualizar una etiqueta (solo para ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTag(
            @PathVariable Long id,
            @RequestBody Tag tagDetails,
            @RequestParam(defaultValue = "false") boolean force
    ) {
        Tag tag = tagService.getTagById(id)
                .orElseThrow(() -> new RuntimeException("Etiqueta no encontrada"));


        if (!force && tag.getItems() != null && !tag.getItems().isEmpty()) {
            throw new TagInUseException("Esta etiqueta está siendo usada por uno o más artículos.");
        }

        tag.setName(tagDetails.getName());
        Tag updatedTag = tagService.updateTag(id, tag, force);
        return ResponseEntity.ok(new TagDTO(updatedTag.getTagId(), updatedTag.getName()));
    }


    // Eliminar una etiqueta (solo para ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
