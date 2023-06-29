package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to POST /requests with RequestHeader X-Sharer-User-Id = {} and body: {}", userId, itemRequestDto);
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to GET /requests with RequestHeader X-Sharer-User-Id = {}", userId);
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Received request to GET /requests/{} with RequestHeader X-Sharer-User-Id = {}", requestId, userId);
        return itemRequestService.getItemRequest(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemPageableRequests(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to GET /requests/all?from={}&size={} with RequestHeader X-Sharer-User-Id = {}", from, size, userId);
        return itemRequestService.getItemPageableRequests(userId, from, size);
    }
}
