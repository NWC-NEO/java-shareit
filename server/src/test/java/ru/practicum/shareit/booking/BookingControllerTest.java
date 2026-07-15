package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void create() throws Exception {
        BookingInputDto input = new BookingInputDto();
        input.setItemId(1L);

        BookingDto output = BookingDto.builder().id(2L).status(Status.WAITING).build();

        when(bookingService.create(eq(1L), any(BookingInputDto.class))).thenReturn(output);

        mockMvc.perform(post("/bookings")
                        .header(userIdHeader, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve() throws Exception {
        BookingDto output = BookingDto.builder().id(2L).status(Status.APPROVED).build();

        when(bookingService.approve(eq(1L), eq(2L), eq(true))).thenReturn(output);

        mockMvc.perform(patch("/bookings/2")
                        .header(userIdHeader, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById() throws Exception {
        BookingDto output = BookingDto.builder().id(2L).build();

        when(bookingService.getById(eq(1L), eq(2L))).thenReturn(output);

        mockMvc.perform(get("/bookings/2")
                        .header(userIdHeader, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getUserBookings() throws Exception {
        BookingDto output = BookingDto.builder().id(2L).build();

        when(bookingService.getUserBookings(eq(1L), eq("ALL"))).thenReturn(List.of(output));

        mockMvc.perform(get("/bookings")
                        .header(userIdHeader, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void getOwnerBookings() throws Exception {
        BookingDto output = BookingDto.builder().id(2L).build();

        when(bookingService.getOwnerBookings(eq(1L), eq("ALL"))).thenReturn(List.of(output));

        mockMvc.perform(get("/bookings/owner")
                        .header(userIdHeader, 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
