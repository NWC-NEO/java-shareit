package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestService;

    @Test
    void create() throws Exception {
        ItemRequestDto input = ItemRequestDto.builder().description("Need item").build();
        ItemRequestDto output = ItemRequestDto.builder().id(1L).description("Need item").build();

        when(requestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(output);

        mockMvc.perform(post("/requests")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need item"));
    }

    @Test
    void getUserRequests() throws Exception {
        ItemRequestDto output = ItemRequestDto.builder().id(1L).build();

        when(requestService.getUserRequests(1L)).thenReturn(List.of(output));

        mockMvc.perform(get("/requests")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getAllRequests() throws Exception {
        ItemRequestDto output = ItemRequestDto.builder().id(1L).build();

        when(requestService.getAllRequests(1L)).thenReturn(List.of(output));

        mockMvc.perform(get("/requests/all")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestDto output = ItemRequestDto.builder().id(1L).build();

        when(requestService.getRequestById(1L, 10L)).thenReturn(output);

        mockMvc.perform(get("/requests/10")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
