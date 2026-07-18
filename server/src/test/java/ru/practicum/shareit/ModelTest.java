package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void testModelsAndDtos() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("email@mail.com");
        assertEquals(1L, user.getId());
        assertEquals("Name", user.getName());
        assertEquals("email@mail.com", user.getEmail());

        Item item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setRequestId(3L);
        item.setOwner(user);
        assertEquals(2L, item.getId());
        assertEquals("Item", item.getName());
        assertEquals("Desc", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(3L, item.getRequestId());
        assertEquals(user, item.getOwner());

        Comment comment = new Comment();
        LocalDateTime now = LocalDateTime.now();
        comment.setId(4L);
        comment.setText("Text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now);
        assertEquals(4L, comment.getId());
        assertEquals("Text", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
        assertEquals(now, comment.getCreated());

        ItemRequest request = new ItemRequest();
        request.setId(5L);
        request.setDescription("Req Desc");
        request.setRequestor(user);
        request.setCreated(now);
        assertEquals(5L, request.getId());
        assertEquals("Req Desc", request.getDescription());
        assertEquals(user, request.getRequestor());
        assertEquals(now, request.getCreated());

        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(10L);
        bookingInputDto.setStart(now);
        bookingInputDto.setEnd(now.plusDays(1));
        assertEquals(10L, bookingInputDto.getItemId());
        assertEquals(now, bookingInputDto.getStart());
        assertEquals(now.plusDays(1), bookingInputDto.getEnd());

        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(11L);
        bookingItemDto.setBookerId(12L);
        assertEquals(11L, bookingItemDto.getId());
        assertEquals(12L, bookingItemDto.getBookerId());

        CommentDto commentDto = new CommentDto();
        commentDto.setId(13L);
        commentDto.setText("Comment text");
        commentDto.setAuthorName("Author");
        commentDto.setCreated(now);
        assertEquals(13L, commentDto.getId());
        assertEquals("Comment text", commentDto.getText());
        assertEquals("Author", commentDto.getAuthorName());
        assertEquals(now, commentDto.getCreated());

        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(14L);
        itemResponseDto.setName("ItemResp");
        itemResponseDto.setDescription("ItemRespDesc");
        itemResponseDto.setAvailable(false);
        itemResponseDto.setLastBooking(bookingItemDto);
        itemResponseDto.setNextBooking(bookingItemDto);
        itemResponseDto.setComments(List.of(commentDto));
        assertEquals(14L, itemResponseDto.getId());
        assertEquals("ItemResp", itemResponseDto.getName());
        assertEquals("ItemRespDesc", itemResponseDto.getDescription());
        assertFalse(itemResponseDto.getAvailable());
        assertEquals(bookingItemDto, itemResponseDto.getLastBooking());
        assertEquals(bookingItemDto, itemResponseDto.getNextBooking());
        assertEquals(1, itemResponseDto.getComments().size());
    }
}
