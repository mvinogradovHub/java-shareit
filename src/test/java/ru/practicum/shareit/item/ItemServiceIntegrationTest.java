package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE , properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {
    private final UserService userService;
    private final ItemService itemService;
    private UserDto user;

    @BeforeEach
    public void init() {
        UserDto owner = UserDto.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        user = userService.addUser(owner);

        ItemDto item = ItemDto.builder()
                .name("Отвертка малеНькая")
                .available(true)
                .description("Супер откручивалка")
                .owner(user)
                .build();

        ItemDto item2 = ItemDto.builder()
                .name("Супер Отвертка большая")
                .available(true)
                .description("Откручивалка")
                .owner(user)
                .build();
        ItemDto item3 = ItemDto.builder()
                .name("Железяка")
                .available(false)
                .description("Железяка")
                .owner(user)
                .build();

        itemService.addItem(item, user.getId());
        itemService.addItem(item2, user.getId());
        itemService.addItem(item3, user.getId());

    }


    @Test
    void searchItems_whenSearchAnyCase_thenReturnItem() {
        List<ItemDto> foundItems = itemService.searchItems(user.getId(), "маленькая", 0, 10);
        assertEquals(foundItems.size(), 1);
        assertEquals(foundItems.get(0).getName(), "Отвертка малеНькая");
    }

    @Test
    void searchItems_whenSearchNameAndDescription_thenReturnAllItem() {
        List<ItemDto> foundItems = itemService.searchItems(user.getId(), "Супер", 0, 10);
        assertEquals(foundItems.size(), 2);
    }

    @Test
    void searchItems_whenSearchNotAvailableItem_thenNotReturnItem() {
        List<ItemDto> foundItems = itemService.searchItems(user.getId(), "Железяка", 0, 10);
        assertEquals(foundItems.size(), 0);
    }
}
