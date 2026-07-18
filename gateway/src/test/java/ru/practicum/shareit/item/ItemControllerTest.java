package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_whenValid_returnsOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item Name");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        Mockito.when(itemClient.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item Name"));
    }

    @Test
    void addItem_whenInvalid_returnsBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_returnsOk() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Name");

        Mockito.when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(new ResponseEntity<>(itemDto, HttpStatus.OK));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void getItemById_returnsOk() throws Exception {
        Mockito.when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getItemsByOwner_returnsOk() throws Exception {
        Mockito.when(itemClient.getItemsByOwner(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_returnsOk() throws Exception {
        Mockito.when(itemClient.searchItems(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "search query"))
                .andExpect(status().isOk());
    }

    @Test
    void addComment_whenValid_returnsOk() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Great item!");

        Mockito.when(itemClient.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(new ResponseEntity<>(commentDto, HttpStatus.OK));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item!"));
    }

    @Test
    void addComment_whenInvalid_returnsBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
