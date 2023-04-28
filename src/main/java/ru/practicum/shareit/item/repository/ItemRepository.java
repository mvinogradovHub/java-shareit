package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    List<Item> getItems(Long userId);

    Item getItemById(Long id);

    List<Item> searchItems(String text);

}
