package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Owner");

        item = new Item();
        item.setId(10L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
    }

    @Test
    void createItem_whenUserExists_savesItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto result = itemService.createItem(1L, itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }

    @Test
    void updateItem_whenNotOwner_throwsNotFoundException() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, 10L, itemDto));
    }

    @Test
    void updateItem_whenOwner_updatesFields() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        ItemDto updateDto = new ItemDto();
        updateDto.setName("NewName");
        updateDto.setDescription("NewDesc");
        updateDto.setAvailable(false);

        ItemResponseDto result = itemService.updateItem(1L, 10L, updateDto);

        assertNotNull(result);
        assertEquals("NewName", result.getName());
        assertEquals("NewDesc", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void getItemById_returnsItemWithBookings_forOwner() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        Booking lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setBooker(user);

        Booking nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setBooker(user);

        when(bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(eq(10L), any(), eq(Status.APPROVED)))
                .thenReturn(lastBooking);
        when(bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(eq(10L), any(), eq(Status.APPROVED)))
                .thenReturn(nextBooking);

        ItemResponseDto result = itemService.getItemById(10L, 1L);

        assertNotNull(result);
        assertNotNull(result.getLastBooking());
        assertNotNull(result.getNextBooking());
    }

    @Test
    void searchItems_whenTextEmpty_returnsEmptyList() {
        List<ItemResponseDto> result = itemService.searchItems("");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_whenTextNotEmpty_returnsItems() {
        when(itemRepository.search("text")).thenReturn(List.of(item));

        List<ItemResponseDto> result = itemService.searchItems("text");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void addComment_whenBookingAllowed_savesComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Good");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Good");
        comment.setAuthor(user);
        comment.setItem(item);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.isCommentAllowed(eq(1L), eq(10L), eq(Status.APPROVED), any())).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(1L, 10L, commentDto);

        assertNotNull(result);
        assertEquals("Good", result.getText());
    }

    @Test
    void addComment_whenNoBooking_throwsValidationException() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Good");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.isCommentAllowed(eq(1L), eq(10L), eq(Status.APPROVED), any())).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 10L, commentDto));
    }
}
