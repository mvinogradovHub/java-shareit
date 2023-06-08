package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemRequestService itemRequestService;

    private BookingWithoutObjDto bookingWithoutObjDto;

    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        bookingWithoutObjDto = BookingWithoutObjDto.builder()
                .itemId(1L)
                .end(LocalDateTime.of(2023, 11, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 10, 10, 0, 0, 0, 0))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();

        bookingDto = BookingDto.builder()
                .item(ItemDto.builder().id(1L).build())
                .end(LocalDateTime.of(2023, 11, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 10, 10, 0, 0, 0, 0))
                .booker(UserDto.builder().id(1L).build())
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();
    }

    @SneakyThrows
    @Test
    void addBooking_whenInvoke_thenReturnBookingDto() {
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).addBooking(bookingWithoutObjDto, 1L);
    }

    @SneakyThrows
    @Test
    void addBooking_whenStartPast_thenReturnBadRequest() {
        bookingWithoutObjDto.setStart(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0));
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    @Test
    void addBooking_whenStartNull_thenReturnBadRequest() {
        bookingWithoutObjDto.setStart(null);
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    @Test
    void addBooking_whenEndPast_thenReturnBadRequest() {
        bookingWithoutObjDto.setEnd(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0));
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    @Test
    void addBooking_whenEndNull_thenReturnBadRequest() {
        bookingWithoutObjDto.setEnd(null);
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    @Test
    void addBooking_whenItemIdNull_thenReturnBadRequest() {
        bookingWithoutObjDto.setItemId(null);
        when(bookingService.addBooking(bookingWithoutObjDto, 1L)).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings").contentType("application/json").header("X-Sharer-User-Id", 1L).content(objectMapper.writeValueAsString(bookingWithoutObjDto))).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
    }

    @SneakyThrows
    @Test
    void approvedBooking_whenInvoke_thenReturnBookingDto() {
        when(bookingService.approvedBooking(1L, true, 1L)).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1L).contentType("application/json").header("X-Sharer-User-Id", 1L).param("approved", "true")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
        verify(bookingService).approvedBooking(1L, true, 1L);
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoke_thenReturnBookingDto() {
        when(bookingService.getBooking(1L, 1L)).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBooking(1L, 1L);
        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBookerBookings_whenInvoke_thenReturnBookingDtoList() {
        when(bookingService.getBookerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .param("state", "ALL")
                .param("from", "13")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookerBookings("ALL", 1L, 13, 10);
        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void getBookerBookings_whenInvokeWithStateNull_thenInvokeGetBookerBookingsWithStateAll() {
        when(bookingService.getBookerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookerBookings("ALL", 1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getBookerBookings_whenInvokeWithSizeNull_thenInvokeGetBookerBookingsWithSize10() {
        when(bookingService.getBookerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookerBookings("ALL", 1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getBookerBookings_whenInvokeWithFromNull_thenInvokeGetBookerBookingsWithFromZero() {
        when(bookingService.getBookerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .param("from", "13")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookerBookings("ALL", 1L, 13, 10);
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_whenInvoke_thenReturnBookingDtoList() {
        when(bookingService.getOwnerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .param("state", "ALL")
                .param("from", "13")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getOwnerBookings("ALL", 1L, 13, 10);
        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_whenInvokeWithStateNull_thenInvokeGetBookerBookingsWithStateAll() {
        when(bookingService.getOwnerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getOwnerBookings("ALL", 1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_whenInvokeWithSizeNull_thenInvokeGetBookerBookingsWithSize10() {
        when(bookingService.getOwnerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getOwnerBookings("ALL", 1L, 0, 10);
    }

    @SneakyThrows
    @Test
    void getOwnerBookings_whenInvokeWithFromNull_thenInvokeGetBookerBookingsWithFromZero() {
        when(bookingService.getOwnerBookings("ALL", 1L, 13, 10)).thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .param("from", "13")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getOwnerBookings("ALL", 1L, 13, 10);
    }
}
