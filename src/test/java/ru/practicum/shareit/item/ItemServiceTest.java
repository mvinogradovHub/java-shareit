package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemService itemService;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Pageable> pageArgumentCaptor;
    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void init() {
        user = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();

        itemRequest = ItemRequest.builder()
                .id(5L)
                .requestor(user)
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .items(new ArrayList<>())
                .description("Описание запроса")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Ответртка")
                .available(true)
                .description("Описание")
                .request(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Ответртка")
                .description("Описание")
                .owner(userDto)
                .available(true)
                .requestId(5L)
                .build();

        comment = Comment.builder()
                .created(LocalDateTime.of(2023, 4, 10, 0, 0, 0, 0))
                .item(item)
                .author(user)
                .text("Текст")
                .id(1L)
                .build();

        booking = Booking.builder()
                .item(item)
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .booker(user)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();
    }

    @Test
    void addItem_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, user.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void addItem_whenItemRequestNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(itemDto, user.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void addItem_whenItemAdd_thenReturnedItem() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(Mockito.any())).thenReturn(ItemMapper.itemDtoToItem(itemDto, user, ItemRequestMapper.itemRequestToItemRequestDto(itemRequest)));

        ItemDto actualItemDto = itemService.addItem(itemDto, user.getId());

        assertEquals(itemDto, actualItemDto);
        verify(itemRepository).save(Mockito.any());
    }

    @Test
    void updateItem_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, user.getId(), itemDto.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void updateItem_whenItemNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, user.getId(), itemDto.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void updateItem_whenUserNotHaveRightsToEdit_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 99L, itemDto.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void updateItem_whenUserAndItemFound_thenUpdateItem() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(Mockito.any())).thenReturn(ItemMapper.itemDtoToItem(itemDto, user, ItemRequestMapper.itemRequestToItemRequestDto(itemRequest)));
        ItemDto newItemDto = ItemDto.builder().id(2L).name("Железяка").description("Описание2").owner(UserDto.builder().id(33L).build()).available(false).requestId(6L).build();

        ItemDto actualItem = itemService.updateItem(newItemDto, user.getId(), item.getId());
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals(savedItem.getName(), newItemDto.getName());
        assertEquals(savedItem.getDescription(), newItemDto.getDescription());
        assertEquals(savedItem.getAvailable(), newItemDto.getAvailable());
        assertNotEquals(savedItem.getId(), newItemDto.getId());
        assertNotEquals(savedItem.getOwner().getId(), newItemDto.getOwner().getId());
        assertNotEquals(savedItem.getRequest().getId(), newItemDto.getRequestId());
    }

    @Test
    void getItem_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), itemDto.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void getItem_whenItemNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItem(user.getId(), itemDto.getId()));
        verify(itemRepository, never()).save(ItemMapper.itemDtoToItem(itemDto, user, null));
    }

    @Test
    void getItem_whenInvoke_returnedItemDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.getItemLastBooking(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(booking));
        when(bookingRepository.getItemNextBooking(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(booking));

        ItemDto actualItemDto = itemService.getItem(user.getId(), item.getId());

        assertEquals(actualItemDto.getName(), item.getName());
        assertEquals(actualItemDto.getDescription(), item.getDescription());
        assertEquals(actualItemDto.getAvailable(), item.getAvailable());
        assertEquals(actualItemDto.getId(), item.getId());
        assertEquals(actualItemDto.getOwner().getId(), item.getOwner().getId());
        assertEquals(actualItemDto.getLastBooking().getId(), booking.getId());
        assertEquals(actualItemDto.getNextBooking().getId(), booking.getId());
    }

    @Test
    void getItems_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItems(user.getId(), 0, 10));
        verify(itemRepository, never()).searchItems(Mockito.any(), Mockito.any());
    }

    @Test
    void getItems_whenInvoke_thenReturnListItemsDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(Mockito.anyLong(), Mockito.any())).thenReturn(List.of(item));
        when(commentRepository.findByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        when(bookingRepository.getItemLastBooking(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(booking));
        when(bookingRepository.getItemNextBooking(Mockito.anyLong(), Mockito.any())).thenReturn(Optional.of(booking));

        List<ItemDto> actualItemDtoList = itemService.getItems(user.getId(), 0, 10);

        assertEquals(actualItemDtoList.size(), 1);
        assertEquals(actualItemDtoList.get(0).getId(), item.getId());
    }

    @Test
    void searchItems_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.searchItems(user.getId(), "текст", 0, 10));
        verify(itemRepository, never()).searchItems(Mockito.any(), Mockito.any());
    }

    @Test
    void searchItems_whenInvoke_thenReturnListItemsDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.searchItems(Mockito.anyString(), Mockito.any())).thenReturn(List.of(item));

        List<ItemDto> actualItemDtoList = itemService.searchItems(user.getId(), "текст", 0, 10);

        assertEquals(actualItemDtoList.size(), 1);
        assertEquals(actualItemDtoList.get(0).getId(), item.getId());
    }

    @Test
    void convertToPageSettings_whenInvokeStart13_thenInvokeWithPage3() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.searchItems(Mockito.anyString(), Mockito.any())).thenReturn(List.of(item));

        List<ItemDto> actualItemDtoList = itemService.searchItems(user.getId(), "текст", 13, 5);

        verify(itemRepository).searchItems(Mockito.anyString(), pageArgumentCaptor.capture());
        Pageable savedPage = pageArgumentCaptor.getValue();

        assertEquals(3, savedPage.getPageNumber());
    }

    @Test
    void addComment_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(new CommentDto(), 1L, 1L));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addComment_whenItemNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(new CommentDto(), 1L, 1L));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addComment_whenInvoke_thenReturnedCommentDto() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.getByBookerAndItemPastApprovedBooking(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));
        when(commentRepository.save(Mockito.any())).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(CommentMapper.commentToCommentDto(comment), user.getId(), item.getId());

        assertEquals(comment.getId(), commentDto.getId());
    }

}
