package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/** TODO Sprint add-controllers. */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
  private final UserService userService;

  @PostMapping
  public UserDto addUser(@Valid @RequestBody UserDto userDto) {
    log.info("Received request to POST /users with body: {}", userDto);
    return userService.addUser(userDto);
  }

  @PatchMapping("/{userId}")
  public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
    log.info("Received request to PUT /users with body: {}", userDto);
    return userService.updateUser(userDto, userId);
  }

  @GetMapping
  public List<UserDto> getUsers() {
    log.info("Received request to GET /users");
    return userService.getUsers();
  }

  @GetMapping("/{id}")
  public UserDto getUserById(@PathVariable Long id) {
    log.info("Received request to GET /users/{}", id);
    return userService.getUserById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteUser(@PathVariable Long id) {
    log.info("Received request to DELETE /users/{}", id);
    userService.deleteUser(id);
  }
}
