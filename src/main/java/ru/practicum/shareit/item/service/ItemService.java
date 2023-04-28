package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utils.ItemValidator;
import ru.practicum.shareit.user.utils.UserValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserValidator userValidator;
    private final ItemValidator itemValidator;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        userValidator.checkUserInRepository(userId);
        itemDto.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.addItem(ItemMapper.toItem(itemDto)));
    }

    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        userValidator.checkUserInRepository(userId);
        itemValidator.checkItemOwner(userId, itemId);
        Item item = itemRepository.getItemById(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.updateItem(item));
    }

    public ItemDto getItem(Long userId, Long itemId) {
        userValidator.checkUserInRepository(userId);
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    public List<ItemDto> getItems(Long userId) {
        userValidator.checkUserInRepository(userId);
        return itemRepository.getItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(Long userId, String text) {
        userValidator.checkUserInRepository(userId);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }


}
