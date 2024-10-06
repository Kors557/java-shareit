package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    User getUser(long userId);

    User createUser(UserDto userDto);

    User updateUser(long userId, UserDto userDto);

    void deleteUser(long userId);
}