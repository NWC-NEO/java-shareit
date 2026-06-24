package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Validated CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @PathVariable Long itemId,
                                      @RequestBody ItemDto itemDto) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemResponseDto> getItemsByOwnerId(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemService.getItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
