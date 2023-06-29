package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        itemRequestDto.setCreated(LocalDateTime.now());
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequestRepository.save(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user)));
    }

    public List<ItemRequestDto> getItemRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        return ItemRequestMapper.listItemRequestToListItemRequestDto(itemRequestRepository.findByRequestorIdOrderByIdDesc(userId));
    }

    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request with ID " + requestId + " not found"));
        return ItemRequestMapper.itemRequestToItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> getItemPageableRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Pageable pageableRequest = convertToPageSettings(from, size, "id");
        return ItemRequestMapper.listItemRequestToListItemRequestDto(itemRequestRepository.findByIdNot(userId, pageableRequest));
    }

    public Pageable convertToPageSettings(Integer from, Integer size, String sortingByField) {
        int page = from >= 0 ? Math.round((float) from / size) : -1;
        return PageRequest.of(page, size, Sort.by(sortingByField).descending());
    }
}
