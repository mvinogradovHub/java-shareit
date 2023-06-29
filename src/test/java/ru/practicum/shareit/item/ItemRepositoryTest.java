package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = { "db.name=test"})
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Pageable pageable;

    @BeforeEach
    public void addItems() {
        User owner = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();
        User user = userRepository.save(owner);

        Item item = Item.builder()
                .name("Отвертка малеНькая")
                .available(true)
                .description("Супер откручивалка")
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .name("Супер Отвертка большая")
                .available(true)
                .description("Откручивалка")
                .owner(user)
                .build();
        Item item3 = Item.builder()
                .name("Железяка")
                .available(false)
                .description("Железяка")
                .owner(user)
                .build();

        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);

        pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    }

    @AfterEach
    public void deleteItems() {

        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void searchItems_whenSearchAnyCase_thenReturnItem() {
        List<Item> foundItems = itemRepository.searchItems("маленькая", pageable);
        assertEquals(foundItems.size(), 1);
        assertEquals(foundItems.get(0).getName(), "Отвертка малеНькая");
    }

    @Test
    void searchItems_whenSearchNameAndDescription_thenReturnAllItem() {
        List<Item> foundItems = itemRepository.searchItems("Супер", pageable);
        assertEquals(foundItems.size(), 2);
    }

    @Test
    void searchItems_whenSearchNotAvailableItem_thenNotReturnItem() {
        List<Item> foundItems = itemRepository.searchItems("Железяка", pageable);
        assertEquals(foundItems.size(), 0);
    }
}
