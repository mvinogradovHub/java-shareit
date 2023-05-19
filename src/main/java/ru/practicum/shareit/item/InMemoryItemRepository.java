package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item addItem(Item item) {
        item.setId(id);
        items.put(id, item);
        id++;
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public List<Item> getItems(Long userId) {
        return items.values().stream().filter(a -> a.getOwner().getId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        return items.get(id);
    }

    @Override
    public List<Item> searchItems(String text) {
        return items.values().stream()
                .filter(a -> (a.getName().toLowerCase().contains(text.toLowerCase())
                        || a.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && a.getAvailable())
                .collect(Collectors.toList());
    }
}
