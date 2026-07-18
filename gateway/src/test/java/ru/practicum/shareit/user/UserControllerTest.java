package ru.practicum.shareit.user;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_whenValid_returnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Valid Name");
        userDto.setEmail("valid@mail.com");

        Mockito.when(userClient.createUser(any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Valid Name"))
                .andExpect(jsonPath("$.email").value("valid@mail.com"));
    }

    @Test
    void createUser_whenInvalidEmail_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Valid Name");
        userDto.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createUser_whenBlankName_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("");
        userDto.setEmail("valid@mail.com");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_whenValid_returnsOk() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated Name");
        userDto.setEmail("updated@mail.com");

        Mockito.when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(new ResponseEntity<>(userDto, HttpStatus.OK));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void updateUser_whenInvalidEmail_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("invalid-email");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_returnsOk() throws Exception {
        Mockito.when(userClient.getUserById(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_returnsOk() throws Exception {
        Mockito.when(userClient.getAllUsers())
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_returnsOk() throws Exception {
        Mockito.when(userClient.deleteUser(anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
