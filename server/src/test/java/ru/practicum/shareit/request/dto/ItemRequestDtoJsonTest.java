package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDtoSerialization() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Щетка");
        itemDto.setDescription("Для обуви");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        itemDto.setOwnerId(5L);

        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна щетка")
                .created(LocalDateTime.of(2026, 7, 13, 10, 0, 0))
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна щетка");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-07-13T10:00:00");

        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Щетка");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
    }
}
