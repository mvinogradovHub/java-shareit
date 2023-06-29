package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserRequestDto userDto) {
        log.info("Received request to POST /users with body: {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserRequestDto userDto, @PathVariable Long userId) {
        log.info("Received request to PUT /users with body: {}", userDto);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Received request to GET /users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Received request to GET /users/{}", id);
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("Received request to DELETE /users/{}", id);
        userClient.deleteUser(id);
    }
}
