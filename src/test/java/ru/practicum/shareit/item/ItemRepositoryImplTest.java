package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryImplTest {

    private ItemRepositoryImpl itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepositoryImpl();
    }

    @Test
    void search_ignoresCase_and_returnsOnlyAvailable() {
        Item item1 = new Item();
        item1.setName("Drill");
        item1.setDescription("Good");
        item1.setAvailable(true);
        item1.setOwnerId(1L);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Broken drill");
        item2.setDescription("Bad");
        item2.setAvailable(false);
        item2.setOwnerId(1L);
        itemRepository.save(item2);

        List<Item> result = itemRepository.search("dRiLl");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
    }

    @Test
    void search_whenBlankText_returnsEmptyList() {
        assertTrue(itemRepository.search("   ").isEmpty());
    }
}
