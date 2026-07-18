package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemRequestMapperTest {

    @Test
    void toDto() {
        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .created(LocalDateTime.now())
                .build();

        ItemDto itemDto = new ItemDto();
        itemDto.setId(2L);
        List<ItemDto> items = List.of(itemDto);

        ItemRequestDto dto = ItemRequestMapper.toDto(request, items);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Description", dto.getDescription());
        assertEquals(items, dto.getItems());
    }
}
