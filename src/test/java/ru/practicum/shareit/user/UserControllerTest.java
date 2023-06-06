package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class UserControllerTest {

  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;
  @MockBean private BookingService bookingService;

  @MockBean private ItemService itemService;

  @MockBean private ItemRequestService itemRequestService;

  private UserDto userDto;

  @BeforeEach
  void init() {

    userDto = UserDto.builder().id(1L).email("mail@mail.ru").name("Misha").build();
  }

  @SneakyThrows
  @Test
  void addUser_whenInvoke_returnUser() {
    when(userService.addUser(userDto)).thenReturn(userDto);

    String result =
        mockMvc
            .perform(
                post("/users")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertEquals(objectMapper.writeValueAsString(userDto), result);
  }

  @SneakyThrows
  @Test
  void addUser_whenInvokeWithEmptyNameUser_returnBadRequest() {
    userDto.setName("");

    when(userService.addUser(userDto)).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void addUser_whenInvokeWithEmptyEmailUser_returnBadRequest() {
    userDto.setEmail("");

    when(userService.addUser(userDto)).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void addUser_whenInvokeWithBadMailUser_returnBadRequest() {
    userDto.setEmail("badMail");

    when(userService.addUser(userDto)).thenReturn(userDto);

    mockMvc
        .perform(
            post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void updateUser_whenInvoke_returnUser() {
    when(userService.updateUser(userDto, userDto.getId())).thenReturn(userDto);

    String result =
        mockMvc
            .perform(
                patch("/users/{userId}", userDto.getId())
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertEquals(objectMapper.writeValueAsString(userDto), result);
  }

  @SneakyThrows
  @Test
  void updateUser_whenInvokeWithEmptyNameUser_returnUserDto() {
    userDto.setName("");

    when(userService.addUser(userDto)).thenReturn(userDto);

    mockMvc
        .perform(
            patch("/users/{userId}", userDto.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void updateUser_whenInvokeWithEmptyEmail_returnUserDto() {
    userDto.setEmail("");

    when(userService.addUser(userDto)).thenReturn(userDto);

    mockMvc
        .perform(
            patch("/users/{userId}", userDto.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDto)))
        .andExpect(status().isOk());
  }

  @SneakyThrows
  @Test
  void getUsers_whenInvoke_returnUsersList() {
    mockMvc.perform(get("/users")).andExpect(status().isOk());

    verify(userService).getUsers();
  }

  @SneakyThrows
  @Test
  void getUserById_whenInvoke_returnUser() {

    mockMvc.perform(get("/users/{id}", userDto.getId())).andExpect(status().isOk());

    verify(userService).getUserById(userDto.getId());
  }

  @SneakyThrows
  @Test
  void deleteUser_whenInvoke_returnStatusOk() {
    mockMvc.perform(delete("/users/{id}", userDto.getId())).andExpect(status().isOk());

    verify(userService).deleteUser(userDto.getId());
  }
}
