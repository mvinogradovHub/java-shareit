package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @InjectMocks private UserService userService;
  @Mock private UserRepository userRepository;
  @Captor private ArgumentCaptor<User> userArgumentCaptor;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void init() {
    user = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();
    userDto = UserDto.builder().id(1L).email("mail@mail.ru").name("Misha").build();
  }

  @Test
  void addUser() {}

  @Test
  void addItem_whenItemAdd_thenReturnedItem() {
    when(userRepository.save(user)).thenReturn(user);

    UserDto actualUserDto = userService.addUser(userDto);

    assertEquals(actualUserDto, userDto);
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_whenInvoke_thenReturnedUserDto() {
    when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);

    UserDto actualUserDto = userService.updateUser(userDto, userDto.getId());

    assertEquals(actualUserDto, userDto);
    verify(userRepository).save(user);
  }

  @Test
  void updateUser_whenUserNotFound_thenNotFoundException() {
    when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, userDto.getId()));
    verify(userRepository, never()).save(Mockito.any());
  }

  @Test
  void updateUser_whenUpdate_thenUpdateOnlyNameAndEmail() {
    when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    UserDto updateUser = UserDto.builder().id(3L).email("newMail@mail.ru").name("vasia").build();

    userService.updateUser(updateUser, userDto.getId());
    verify(userRepository).save(userArgumentCaptor.capture());
    User savedUser = userArgumentCaptor.getValue();

    assertEquals(savedUser.getName(), "vasia");
    assertEquals(savedUser.getEmail(), "newMail@mail.ru");
    assertNotEquals(savedUser.getId(), 3L);
  }

  @Test
  void deleteUser_whenInvoke_thenInvokeDeleteById() {
    userService.deleteUser(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void getUsers_whenInvoke_thenReturnedUserDtoList() {
    when(userRepository.findAll()).thenReturn(List.of(user));

    List<UserDto> users = userService.getUsers();

    assertEquals(users.size(), 1);
    assertEquals(users.get(0).getId(), user.getId());
  }

  @Test
  void getUserById_whenUserNotFound_thenNotFoundException() {
    when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    verify(userRepository, never()).save(Mockito.any());
  }

  @Test
  void getUserById_whenInvoke_returnedUserDto() {
    when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

    UserDto actualUserDto = userService.getUserById(1L);
    assertEquals(actualUserDto.getName(), user.getName());
    assertEquals(actualUserDto.getId(), user.getId());
    assertEquals(actualUserDto.getEmail(), user.getEmail());
  }
}
