package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemControllerTest {
  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;
  @MockBean private UserService userService;
  @MockBean private BookingService bookingService;
  @MockBean private ItemService itemService;
  @MockBean private ItemRequestService itemRequestService;

  ItemDto itemDto;
  CommentDto commentDto;

  @BeforeEach
  void init() {
    itemDto =
        ItemDto.builder()
            .id(1L)
            .name("Ответртка")
            .description("Описание")
            .owner(UserDto.builder().id(1L).build())
            .available(true)
            .requestId(5L)
            .build();

    commentDto =
        CommentDto.builder()
            .created(LocalDateTime.of(2023, 4, 10, 0, 0, 0, 0))
            .text("Текст")
            .id(1L)
            .build();
  }

  @SneakyThrows
  @Test
  void addItem_whenInvoke_thenReturnItemDto() {
    when(itemService.addItem(itemDto, 1L)).thenReturn(itemDto);

    String result =
        mockMvc
            .perform(
                post("/items")
                    .contentType("application/json")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(itemDto), result);
    verify(itemService).addItem(itemDto, 1L);
  }

  @SneakyThrows
  @Test
  void addItem_whenItemNameEmpty_thenReturnBadRequest() {
    itemDto.setName("");

    mockMvc
        .perform(
            post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void addItem_whenItemDescriptionEmpty_thenReturnBadRequest() {
    itemDto.setDescription("");

    mockMvc
        .perform(
            post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void addItem_whenItemAvailableNull_thenReturnBadRequest() {
    itemDto.setAvailable(null);

    mockMvc
        .perform(
            post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(itemDto)))
        .andExpect(status().isBadRequest());
  }

  @SneakyThrows
  @Test
  void updateItem_whenInvoke_thenReturnItemDto() {
    when(itemService.updateItem(itemDto, 1L, itemDto.getId())).thenReturn(itemDto);

    String result =
        mockMvc
            .perform(
                patch("/items/{itemId}", itemDto.getId())
                    .contentType("application/json")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(itemDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(itemDto), result);
    verify(itemService).updateItem(itemDto, 1L, itemDto.getId());
  }

  @SneakyThrows
  @Test
  void getItem_whenInvoke_thenReturnItemDto() {
    when(itemService.getItem(1L, itemDto.getId())).thenReturn(itemDto);

    String result =
        mockMvc
            .perform(
                get("/items/{itemId}", itemDto.getId())
                    .contentType("application/json")
                    .header("X-Sharer-User-Id", 1L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(itemDto), result);
    verify(itemService).getItem(1L, itemDto.getId());
  }

  @SneakyThrows
  @Test
  void getItems_whenInvoke_thenReturnedItemDtoList() {
    when(itemService.getItems(1L, 13, 11)).thenReturn(List.of(itemDto));

    String result =
        mockMvc
            .perform(
                get("/items")
                    .header("X-Sharer-User-Id", 1L)
                    .param("from", "13")
                    .param("size", "11"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
    verify(itemService).getItems(1L, 13, 11);
  }

  @SneakyThrows
  @Test
  void getItems_whenFromOrSizeNull_thenForm0AndSize10() {
    when(itemService.getItems(1L, 0, 10)).thenReturn(List.of(itemDto));

    mockMvc
        .perform(get("/items").header("X-Sharer-User-Id", 1L))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8);

    verify(itemService).getItems(1L, 0, 10);
  }

  @SneakyThrows
  @Test
  void searchItem_whenInvoke_thenReturnedItemDtoList() {
    when(itemService.searchItems(1L, "text", 13, 11)).thenReturn(List.of(itemDto));

    String result =
        mockMvc
            .perform(
                get("/items/search")
                    .header("X-Sharer-User-Id", 1L)
                    .param("text", "text")
                    .param("from", "13")
                    .param("size", "11"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
    verify(itemService).searchItems(1L, "text", 13, 11);
  }

  @SneakyThrows
  @Test
  void searchItem_whenFromOrSizeNull_thenForm0AndSize10() {
    when(itemService.searchItems(1L, "text", 0, 10)).thenReturn(List.of(itemDto));

    mockMvc
        .perform(get("/items/search").header("X-Sharer-User-Id", 1L).param("text", "text"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString(StandardCharsets.UTF_8);

    verify(itemService).searchItems(1L, "text", 0, 10);
  }

  @SneakyThrows
  @Test
  void addComment_whenInvoke_thenReturnCommentDto() {
    when(itemService.addComment(commentDto, 1L, itemDto.getId())).thenReturn(commentDto);

    String result =
        mockMvc
            .perform(
                post("/items/{itemId}/comment", itemDto.getId())
                    .contentType("application/json")
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(commentDto)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

    assertEquals(objectMapper.writeValueAsString(commentDto), result);
    verify(itemService).addComment(commentDto, 1L, itemDto.getId());
  }

  @SneakyThrows
  @Test
  void addComment_whenTextNull_whenBadRequest() {
    commentDto.setText(null);

    mockMvc
        .perform(
            post("/items/{itemId}/comment", itemDto.getId())
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(commentDto)))
        .andExpect(status().isBadRequest());
  }
}
