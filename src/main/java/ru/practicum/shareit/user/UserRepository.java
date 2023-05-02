package ru.practicum.shareit.user;


import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    List<User> getUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);
}
