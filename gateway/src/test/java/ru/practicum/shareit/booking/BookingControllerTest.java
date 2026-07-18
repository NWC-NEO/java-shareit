package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void create_whenValid_returnsOk() throws Exception {
        BookingInputDto bookingInputDto = new BookingInputDto();
        bookingInputDto.setItemId(1L);
        bookingInputDto.setStart(LocalDateTime.now().plusDays(1));
        bookingInputDto.setEnd(LocalDateTime.now().plusDays(2));

        Mockito.when(bookingClient.create(anyLong(), any(BookingInputDto.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isOk());
    }

    @Test
    void create_whenInvalid_returnsBadRequest() throws Exception {
        BookingInputDto bookingInputDto = new BookingInputDto();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingInputDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approve_returnsOk() throws Exception {
        Mockito.when(bookingClient.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_returnsOk() throws Exception {
        Mockito.when(bookingClient.getById(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_returnsOk() throws Exception {
        Mockito.when(bookingClient.getUserBookings(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_returnsOk() throws Exception {
        Mockito.when(bookingClient.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE"))
                .andExpect(status().isOk());
    }
}
