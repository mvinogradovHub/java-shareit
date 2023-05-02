package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    List<Item> getItems(Long userId);

    Item getItemById(Long id);

    List<Item> searchItems(String text);

}
