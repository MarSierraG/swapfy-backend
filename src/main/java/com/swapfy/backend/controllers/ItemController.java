package com.swapfy.backend.controllers;

import com.swapfy.backend.models.Item;
import com.swapfy.backend.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
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

    // Crear un nuevo artículo
    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    // Actualizar un artículo existente
    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Item itemDetails) {
        return itemService.updateItem(id, itemDetails);
    }

    // Eliminar un artículo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los artículos disponibles
    @GetMapping("/available")
    public List<Item> getAvailableItems() {
        return itemService.getAvailableItems();
    }

    // Actualizar disponibilidad de un artículo
    @PutMapping("/{id}/availability")
    public ResponseEntity<Item> updateItemAvailability(
            @PathVariable Long id,
            @RequestParam boolean isAvailable
    ) {
        return itemService.updateAvailability(id, isAvailable)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
