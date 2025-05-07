package com.swapfy.backend.services;

import com.swapfy.backend.models.Item;
import com.swapfy.backend.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.swapfy.backend.models.Tag;
import com.swapfy.backend.repositories.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, TagRepository tagRepository) {
        this.itemRepository = itemRepository;
        this.tagRepository = tagRepository;
    }

    public List<Tag> findTagsByIds(List<Long> ids) {
        return tagRepository.findAllById(ids);
    }

    // Obtener todos los artículos
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // Obtener un artículo por ID
    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    // Obtener articulos por ID de usuario
    public List<Item> getItemsByUserId(Long userId) {
        return itemRepository.findByUser_UserId(userId);
    }

    // Crear artículo
    public Item saveItem(Item item) {
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
            item.setStatus(itemDetails.getStatus());
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

    // Obtener artículos disponibles
    public List<Item> getAvailableItems() {
        return itemRepository.findByStatus("Available");
    }

    // Actualizar disponibilidad
    public Optional<Item> updateAvailability(Long id, String status) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        optionalItem.ifPresent(item -> {
            item.setStatus(status);
            itemRepository.save(item);
        });
        return optionalItem;
    }

    // Verificar si el usuario está autorizado para eliminar el artículo
    public boolean isUserAuthorizedToDelete(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            Item foundItem = item.get();
            return foundItem.getUser().getUserId().equals(itemId); // Verifica si el usuario tiene permisos
        }
        return false;
    }
}
