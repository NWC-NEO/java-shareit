package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;

    @Test
    void createItem_thenReturns200() throws Exception {
        ItemDto input = new ItemDto();
        input.setName("Item");
        input.setDescription("Desc");
        input.setAvailable(true);

        ItemDto output = new ItemDto();
        output.setId(1L);
        output.setName("Item");

        when(itemService.createItem(anyLong(), any())).thenReturn(output);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateItem_thenReturns200() throws Exception {
        ItemDto input = new ItemDto();
        input.setName("Updated");

        ItemDto output = new ItemDto();
        output.setId(1L);
        output.setName("Updated");

        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(output);

        mvc.perform(patch("/items/1")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void getItemById_thenReturns200() throws Exception {
        ItemDto output = new ItemDto();
        output.setId(1L);

        when(itemService.getItemById(1L)).thenReturn(output);

        mvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void searchItems_thenReturnsList() throws Exception {
        when(itemService.searchItems(anyString())).thenReturn(List.of(new ItemDto()));

        mvc.perform(get("/items/search").param("text", "query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
