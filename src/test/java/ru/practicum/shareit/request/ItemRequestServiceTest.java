package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Captor
    private ArgumentCaptor<Pageable> pageArgumentCaptor;
    private ItemRequest itemRequest;

    private ItemRequestDto itemRequestDto;

    private User user;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        itemRequest = ItemRequest.builder()
                .id(5L)
                .requestor(user)
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .items(new ArrayList<>())
                .description("Описание запроса")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(5L)
                .requestorId(user.getId())
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .items(new ArrayList<>())
                .description("Описание запроса")
                .build();
    }

    @Test
    void addItemRequest_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(itemRequestDto, user.getId()));
        verify(itemRequestRepository, never()).save(Mockito.any());
    }

    @Test
    void addItemRequest_whenAdd_thenReturnedItemRequestDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto newItemRequestDto = itemRequestService.addItemRequest(itemRequestDto, user.getId());

        assertNotNull(newItemRequestDto.getCreated());
        assertEquals(newItemRequestDto.getId(), itemRequest.getId());
    }

    @Test
    void getItemRequests_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequests(user.getId()));
    }

    @Test
    void getItemRequests_whenInvoke_thenReturnedItemRequestDtoList() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorIdOrderByIdDesc(user.getId())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> newItemRequestDtoList = itemRequestService.getItemRequests(user.getId());

        assertEquals(newItemRequestDtoList.size(), 1);
        assertEquals(newItemRequestDtoList.get(0).getId(), itemRequest.getId());
    }

    @Test
    void getItemRequest_whenInvoke_thenReturnedItemRequestDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto newItemRequestDto = itemRequestService.getItemRequest(user.getId(), itemRequest.getId());

        assertEquals(newItemRequestDto.getId(), itemRequest.getId());
    }

    @Test
    void getItemRequest_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(user.getId(), itemRequest.getId()));
    }

    @Test
    void getItemRequest_whenItemRequestNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(user.getId(), itemRequest.getId()));
    }

    @Test
    void getItemPageableRequests_whenInvoke_thenReturnedItemRequestDtoList() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByIdNot(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> newItemRequestDtoList = itemRequestService.getItemPageableRequests(user.getId(), 0, 10);

        assertEquals(newItemRequestDtoList.size(), 1);
        assertEquals(newItemRequestDtoList.get(0).getId(), itemRequest.getId());
    }

    @Test
    void convertToPageSettings_whenInvokeStart13_thenInvokeWithPage3() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        itemRequestService.getItemPageableRequests(user.getId(), 13, 5);

        verify(itemRequestRepository).findByIdNot(Mockito.anyLong(), pageArgumentCaptor.capture());
        Pageable savedPage = pageArgumentCaptor.getValue();

        assertEquals(3, savedPage.getPageNumber());
    }
}
