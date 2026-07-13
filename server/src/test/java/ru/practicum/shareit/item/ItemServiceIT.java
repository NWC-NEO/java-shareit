package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIT {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getItemsByOwnerId() {
        UserDto ownerDto = new UserDto();
        ownerDto.setName("Owner");
        ownerDto.setEmail("owner@mail.com");
        UserDto owner = userService.createUser(ownerDto);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item 1");
        itemDto.setDescription("Description 1");
        itemDto.setAvailable(true);
        itemService.createItem(owner.getId(), itemDto);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Item 2");
        itemDto2.setDescription("Description 2");
        itemDto2.setAvailable(true);
        itemService.createItem(owner.getId(), itemDto2);

        List<ItemResponseDto> items = itemService.getItemsByOwnerId(owner.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getName()).isEqualTo("Item 1");
        assertThat(items.get(1).getName()).isEqualTo("Item 2");
    }
}
