package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        assertNull(ItemMapper.toItemDto(null));

        Item item = new Item();
        item.setId(1L);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setRequestId(2L);

        ItemDto dtoWithoutOwner = ItemMapper.toItemDto(item);
        assertNotNull(dtoWithoutOwner);
        assertNull(dtoWithoutOwner.getOwnerId());

        User owner = new User();
        owner.setId(3L);
        item.setOwner(owner);

        ItemDto dtoWithOwner = ItemMapper.toItemDto(item);
        assertNotNull(dtoWithOwner);
        assertEquals(3L, dtoWithOwner.getOwnerId());
    }

    @Test
    void toItem() {
        assertNull(ItemMapper.toItem(null));

        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);
        dto.setRequestId(2L);

        Item item = ItemMapper.toItem(dto);
        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Item", item.getName());
        assertEquals("Desc", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(2L, item.getRequestId());
    }
}
