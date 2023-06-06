package ru.practicum.shareit.request;

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
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemRequestControllerTest {
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;
  @MockBean private BookingService bookingService;

  @MockBean private ItemService itemService;

  @MockBean private ItemRequestService itemRequestService;

  ItemRequestDto itemRequestDto;

  @BeforeEach
  void init() {
    itemRequestDto =
        ItemRequestDto.builder()
            .id(5L)
            .requestorId(1L)
            .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
            .items(new ArrayList<>())
            .description("Описание запроса")
            .build();
  }

  @SneakyThrows
  @Test
  void addItemRequest_whenInvoke_thenReturnItemRequestDto() {
    when(itemRequestService.addItemRequest(itemRequestDto, 1L)).thenReturn(itemRequestDto);

    String result =
        mockMvc
            .perform(
                post("/requests")
                    .contentType("application/json")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemRequestDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    verify(itemRequestService).addItemRequest(itemRequestDto, 1L);
  }

  @SneakyThrows
  @Test
  void addItemRequest_whenDescriptionNull_thenBadRequest() {
    itemRequestDto.setDescription(null);
    mockMvc
        .perform(
            post("/requests")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(itemRequestDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void getItemRequests_whenInvoke_thenReturnedItemRequestDtoList() {
    when(itemRequestService.getItemRequests(1L)).thenReturn(List.of(itemRequestDto));

    String result =
        mockMvc
            .perform(get("/requests").header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    verify(itemRequestService).getItemRequests(1L);
  }

  @SneakyThrows
  @Test
  void getItem() {
    when(itemRequestService.getItemRequest(1L, itemRequestDto.getId())).thenReturn(itemRequestDto);

    String result =
        mockMvc
            .perform(
                get("/requests/{requestId}", itemRequestDto.getId()).header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    verify(itemRequestService).getItemRequest(1L, itemRequestDto.getId());
  }

  @SneakyThrows
  @Test
  void getItemPageableRequests_whenInvoke_thenReturnedItemRequestDtoList() {
    when(itemRequestService.getItemPageableRequests(1L, 13, 11))
        .thenReturn(List.of(itemRequestDto));

    String result =
        mockMvc
            .perform(
                get("/requests/all")
                    .header("X-Sharer-User-Id", 1L)
                    .param("from", "13")
                    .param("size", "11"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(List.of(itemRequestDto)), result);
    verify(itemRequestService).getItemPageableRequests(1L, 13, 11);
  }

  @SneakyThrows
  @Test
  void getItemPageableRequests_whenFromOrSizeNull_thenForm0AndSize10() {
    when(itemRequestService.getItemPageableRequests(1L, 0, 10)).thenReturn(List.of(itemRequestDto));

    mockMvc
        .perform(get("/requests/all").header("X-Sharer-User-Id", 1L))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8);

    verify(itemRequestService).getItemPageableRequests(1L, 0, 10);
  }
}
