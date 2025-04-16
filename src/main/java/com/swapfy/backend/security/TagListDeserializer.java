package com.swapfy.backend.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.swapfy.backend.models.Tag;
import com.swapfy.backend.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TagListDeserializer extends JsonDeserializer<List<Tag>> {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Tag> deserialize(JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws IOException {
        // Leer el array de IDs de tags
        String json = p.getText();
        String[] tagIds = json.replace("[", "").replace("]", "").split(",");

        // Buscar los tags en la base de datos usando los IDs
        List<Tag> tags = new ArrayList<>();
        for (String tagId : tagIds) {
            Long id = Long.parseLong(tagId.trim());
            tagRepository.findById(id).ifPresent(tags::add);  // Añadir tag a la lista si se encuentra
        }
        return tags;
    }
}
