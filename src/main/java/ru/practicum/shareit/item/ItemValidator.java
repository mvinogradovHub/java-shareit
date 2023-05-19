package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoEditingRightsException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemValidator {
    private final ItemRepository itemRepository;

    public void checkItemOwner(Long userId, Long itemId) {
        Item itemInRepository = itemRepository.getItemById(itemId);
        if (itemInRepository.getOwner().getId().equals(userId)) {
            return;
        }
        log.warn("The user id {} does not have rights to edit items", userId);
        throw new NoEditingRightsException("User id " + userId + " does not have rights to edit item");
    }
}
