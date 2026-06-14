package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_whenUserExists_thenSavesItem() {
        ItemDto input = new ItemDto();
        input.setName("Item");
        input.setDescription("Desc");
        input.setAvailable(true);

        Item savedItem = new Item();
        savedItem.setId(1L);
        savedItem.setName("Item");

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto result = itemService.createItem(1L, input);

        assertEquals(1L, result.getId());
    }

    @Test
    void createItem_whenUserNotFound_thenThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(1L, new ItemDto()));
    }

    @Test
    void updateItem_whenNotOwner_thenThrowsNotFoundException() {
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setOwnerId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 1L, new ItemDto()));
    }

    @Test
    void searchItems_callsRepository() {
        itemService.searchItems("text");
        verify(itemRepository, times(1)).search("text");
    }
}
