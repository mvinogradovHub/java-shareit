package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody ItemRequestRequestDto itemRequestDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to POST /requests with RequestHeader X-Sharer-User-Id = {} and body: {}", userId, itemRequestDto);
        return itemRequestClient.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Received request to GET /requests with RequestHeader X-Sharer-User-Id = {}", userId);
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("Received request to GET /requests/{} with RequestHeader X-Sharer-User-Id = {}", requestId, userId);
        return itemRequestClient.getItemRequest(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemPageableRequests(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        log.info("Received request to GET /requests/all?from={}&size={} with RequestHeader X-Sharer-User-Id = {}", from, size, userId);
        return itemRequestClient.getItemPageableRequests(userId, from, size);
    }
}
