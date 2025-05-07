package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Item;
import com.swapfy.backend.models.Tag;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.ItemService;
import com.swapfy.backend.services.SecurityService;
import com.swapfy.backend.exceptions.ForbiddenException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.swapfy.backend.dto.ItemRequestDTO;
import com.swapfy.backend.dto.ItemResponseDTO;
import com.swapfy.backend.mappers.ItemMapper;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final SecurityService securityService;


    @Autowired
    public ItemController(ItemService itemService, SecurityService securityService) {
        this.itemService = itemService;
        this.securityService = securityService;
    }


    // Obtener todos los artículos
    @GetMapping
    public List<ItemResponseDTO> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return items.stream()
                .map(ItemMapper::toDTO)
                .toList();
    }


    // Obtener un artículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(i -> ResponseEntity.ok(ItemMapper.toDTO(i)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Obtener todos los artículos por ID de usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemResponseDTO>> getItemsByUser(@PathVariable Long userId) {
        List<Item> items = itemService.getItemsByUserId(userId); // <-- lo debes tener en tu servicio
        List<ItemResponseDTO> dtoList = items.stream()
                .map(ItemMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }


    // Crear un nuevo artículo (solo para el propio usuario autenticado)
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@Valid @RequestBody ItemRequestDTO dto) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build();
        }

        // Buscar las etiquetas por ID
        List<Tag> tags = itemService.findTagsByIds(dto.getTags());

        // Mapear a entidad
        Item item = ItemMapper.toEntity(dto, tags, authenticatedUser);

        // Guardar
        Item savedItem = itemService.saveItem(item);

        // Mapear respuesta
        ItemResponseDTO responseDTO = ItemMapper.toDTO(savedItem);

        return ResponseEntity.ok(responseDTO);
    }


    // Actualizar un artículo existente
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId, @Valid @RequestBody ItemRequestDTO dto) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Optional<Item> existingItemOpt = itemService.getItemById(itemId);
        if (existingItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item existingItem = existingItemOpt.get();
        String roleName = authenticatedUser.getRole().getName();

        if (!"ADMIN".equalsIgnoreCase(roleName) &&
                !existingItem.getUser().getUserId().equals(authenticatedUser.getUserId())) {
            return ResponseEntity.status(403).body("No puedes actualizar un artículo que no te pertenece");
        }

        // Obtener las etiquetas
        List<Tag> tags = itemService.findTagsByIds(dto.getTags());

        // Actualizar campos del artículo existente
        existingItem.setTitle(dto.getTitle());
        existingItem.setDescription(dto.getDescription());
        existingItem.setCreditValue(dto.getCreditValue());
        existingItem.setStatus(dto.getStatus());
        existingItem.setType(dto.getType());
        existingItem.setTags(tags);

        // Guardar cambios
        Item updatedItem = itemService.saveItem(existingItem);

        // Preparar respuesta
        ItemResponseDTO responseDTO = ItemMapper.toDTO(updatedItem);

        return ResponseEntity.ok(responseDTO);
    }



    // Eliminar un artículo
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Optional<Item> optionalItem = itemService.getItemById(itemId);
        if (optionalItem.isEmpty()) {
            return ResponseEntity.status(404).body("Artículo no encontrado");
        }

        Item item = optionalItem.get();
        boolean isOwner = item.getUser().getUserId().equals(authenticatedUser.getUserId());
        boolean isAdmin = "ADMIN".equalsIgnoreCase(authenticatedUser.getRole().getName());

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("No puedes eliminar un artículo que no te pertenece");
        }

        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build(); // 204 sin contenido
    }

    // Obtener todos los artículos disponibles
    @GetMapping("/available")
    public List<ItemResponseDTO> getAvailableItems() {
        List<Item> items = itemService.getAvailableItems();
        return items.stream()
                .map(ItemMapper::toDTO)
                .toList();
    }


    @PutMapping("/{itemId}/availability")
    public ResponseEntity<Item> updateItemAvailability(
            @PathVariable Long itemId,
            @RequestParam String status
    ) {
        if (!status.equalsIgnoreCase("Available") && !status.equalsIgnoreCase("Unavailable")) {
            return ResponseEntity.badRequest().body(null);
        }

        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Item> existingItemOpt = itemService.getItemById(itemId);
        if (existingItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item existingItem = existingItemOpt.get();

        if (!existingItem.getUser().getUserId().equals(authenticatedUser.getUserId()) &&
                !"ADMIN".equalsIgnoreCase(authenticatedUser.getRole().getName())) {
            throw new ForbiddenException("No puedes cambiar la disponibilidad de un artículo que no te pertenece");
        }

        return itemService.updateAvailability(itemId, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
