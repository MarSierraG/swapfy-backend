package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Item;
import com.swapfy.backend.models.User;
import com.swapfy.backend.services.ItemService;
import com.swapfy.backend.services.SecurityService;
import com.swapfy.backend.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    // Obtener un artículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo artículo (solo para el propio usuario autenticado)
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        User authenticatedUser = securityService.getAuthenticatedUser();

        if (authenticatedUser == null) {
            throw new ForbiddenException("No estás autenticado");
        }

        // Sobrescribe el usuario con el del token
        item.setUser(authenticatedUser);

        return itemService.createItem(item);
    }

    // Actualizar un artículo existente
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable Long itemId, @RequestBody Item itemDetails) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        Optional<Item> existingItemOpt = itemService.getItemById(itemId);
        if (existingItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Item existingItem = existingItemOpt.get();

        // DEBUG
        System.out.println("➡️ Usuario autenticado: " + authenticatedUser.getEmail());
        System.out.println("➡️ Rol detectado: " + authenticatedUser.getRole());

        if (!existingItem.getUser().getUserId().equals(authenticatedUser.getUserId()) &&
                !"ADMIN".equalsIgnoreCase(authenticatedUser.getRole())) {
            throw new ForbiddenException("No puedes actualizar un artículo que no te pertenece");
        }

        Item updatedItem = itemService.updateItem(itemId, itemDetails);
        return ResponseEntity.ok(updatedItem);
    }


    // Eliminar un artículo
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        User authenticatedUser = securityService.getAuthenticatedUser();
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        Optional<Item> existingItemOpt = itemService.getItemById(itemId);
        if (existingItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item existingItem = existingItemOpt.get();
        if (!existingItem.getUser().getUserId().equals(authenticatedUser.getUserId()) &&
                !"ADMIN".equalsIgnoreCase(authenticatedUser.getRole())) {
            throw new ForbiddenException("No puedes eliminar un artículo que no te pertenece");
        }
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los artículos disponibles
    @GetMapping("/available")
    public List<Item> getAvailableItems() {
        return itemService.getAvailableItems();
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
                !"ADMIN".equalsIgnoreCase(authenticatedUser.getRole())) {
            throw new ForbiddenException("No puedes cambiar la disponibilidad de un artículo que no te pertenece");
        }

        return itemService.updateAvailability(itemId, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
