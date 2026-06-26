package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemResponseDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return toItemResponseDto(itemRepository.save(item), userId);
    }

    @Transactional
    @Override
    public ItemResponseDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Не владелец");
        }
        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setAvailable(itemDto.getAvailable());
        return toItemResponseDto(existingItem, userId);
    }

    @Override
    public ItemResponseDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        return toItemResponseDto(item, userId);
    }

    @Override
    public List<ItemResponseDto> getItemsByOwnerId(Long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> toItemResponseDto(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.search(text).stream()
                .map(item -> toItemResponseDto(item, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!bookingRepository.existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(userId, itemId, LocalDateTime.now(), Status.APPROVED)) {
            throw new ValidationException("Пользователь не арендовал этот предмет или аренда еще не завершена");
        }
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return mapCommentToDto(commentRepository.save(comment));
    }

    private ItemResponseDto toItemResponseDto(Item item, Long userId) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        if (userId != null && item.getOwner().getId().equals(userId)) {
            Booking last = bookingRepository.findFirstByItem_IdAndStartBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now(), Status.APPROVED);
            Booking next = bookingRepository.findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), Status.APPROVED);
            if (last != null) dto.setLastBooking(mapToBookingItemDto(last));
            if (next != null) dto.setNextBooking(mapToBookingItemDto(next));
        }

        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(this::mapCommentToDto).collect(Collectors.toList());
        dto.setComments(comments);
        return dto;
    }

    private BookingItemDto mapToBookingItemDto(Booking booking) {
        BookingItemDto dto = new BookingItemDto();
        dto.setId(booking.getId());
        dto.setBookerId(booking.getBooker().getId());
        return dto;
    }

    private CommentDto mapCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }
}
