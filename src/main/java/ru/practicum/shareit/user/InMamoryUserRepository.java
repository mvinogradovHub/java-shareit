package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMamoryUserRepository implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User addUser(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }

    @Override
    public User getUserByEmail(String email) {
        List<User> usersList = users.values().stream().filter(a -> a.getEmail().equals(email)).collect(Collectors.toList());
        if (usersList.isEmpty()) {
            return null;
        }
        return usersList.get(0);
    }
}
