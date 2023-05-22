package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItemValidator {

    public void checkItemOwner(Long userId, Item item) {
        if (item.getOwner().getId().equals(userId)) {
            return;
        }
        log.warn("The user id {} does not have rights to edit items", userId);
        throw new NotFoundException("User id " + userId + " does not have rights to edit item");

    }
}
