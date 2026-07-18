package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void search_findsItemsByTextIgnoreCase() {
        User user = new User();
        user.setName("Owner");
        user.setEmail("owner@mail.com");
        userRepository.save(user);

        Item item1 = new Item();
        item1.setName("Дрель электрическая");
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);
        item1.setOwner(user);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Обычная КРЕСТОВАЯ отвертка");
        item2.setAvailable(true);
        item2.setOwner(user);
        itemRepository.save(item2);

        List<Item> result = itemRepository.search("дРеЛь");
        assertEquals(1, result.size());
        assertEquals("Дрель электрическая", result.get(0).getName());

        result = itemRepository.search("крестовая");
        assertEquals(1, result.size());
        assertEquals("Отвертка", result.get(0).getName());
    }
}
