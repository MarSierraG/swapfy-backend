package com.swapfy.backend.services;

import com.swapfy.backend.models.Item;
import com.swapfy.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // Obtener todos los artículos
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Obtener artículo por ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // Crear artículo
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    // Actualizar artículo
    public Item updateItem(Long id, Item itemDetails) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setTitle(itemDetails.getTitle());
            item.setDescription(itemDetails.getDescription());
            item.setCreditValue(itemDetails.getCreditValue());
            item.setAvailable(itemDetails.isAvailable()); // AQUÍ estaba el error
            item.setType(itemDetails.getType());
            item.setTags(itemDetails.getTags());
            return itemRepository.save(item);
        } else {
            throw new RuntimeException("Item not found with id " + id);
        }
    }

    // Eliminar artículo
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    // Obtener solo artículos disponibles
    public List<Item> getAvailableItems() {
        return itemRepository.findByAvailableTrue();
    }

    // Actualizar disponibilidad
    public Optional<Item> updateAvailability(Long id, boolean isAvailable) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        optionalItem.ifPresent(item -> {
            item.setAvailable(isAvailable);
            itemRepository.save(item);
        });
        return optionalItem;
    }
}
