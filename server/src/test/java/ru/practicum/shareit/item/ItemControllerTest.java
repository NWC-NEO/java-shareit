package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void createItem() throws Exception {
        ItemDto inputDto = new ItemDto();
        inputDto.setName("Item");
        inputDto.setDescription("Desc");
        inputDto.setAvailable(true);

        ItemResponseDto outputDto = new ItemResponseDto();
        outputDto.setId(1L);
        outputDto.setName("Item");
        outputDto.setDescription("Desc");
        outputDto.setAvailable(true);

        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/items")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Desc"));
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item");

        CommentDto outputDto = new CommentDto();
        outputDto.setId(1L);
        outputDto.setText("Great item");
        outputDto.setAuthorName("Author");
        outputDto.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(1L), eq(2L), any(CommentDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/items/2/comment")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item"))
                .andExpect(jsonPath("$.authorName").value("Author"));
    }

    @Test
    void updateItem() throws Exception {
        ItemDto inputDto = new ItemDto();
        inputDto.setName("Updated Item");

        ItemResponseDto outputDto = new ItemResponseDto();
        outputDto.setId(1L);
        outputDto.setName("Updated Item");

        when(itemService.updateItem(eq(1L), eq(2L), any(ItemDto.class))).thenReturn(outputDto);

        mockMvc.perform(patch("/items/2")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void getItemById() throws Exception {
        ItemResponseDto outputDto = new ItemResponseDto();
        outputDto.setId(1L);
        outputDto.setName("Item");

        when(itemService.getItemById(eq(1L), eq(2L))).thenReturn(outputDto);

        mockMvc.perform(get("/items/1")
                        .header(userIdHeader, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Item"));
    }

    @Test
    void getItemsByOwnerId() throws Exception {
        ItemResponseDto outputDto = new ItemResponseDto();
        outputDto.setId(1L);

        when(itemService.getItemsByOwnerId(1L)).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/items")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void searchItems() throws Exception {
        ItemResponseDto outputDto = new ItemResponseDto();
        outputDto.setId(1L);

        when(itemService.searchItems("text")).thenReturn(List.of(outputDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
