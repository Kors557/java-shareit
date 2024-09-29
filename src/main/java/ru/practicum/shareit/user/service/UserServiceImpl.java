package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.erorr.exception.ConflictException;
import ru.practicum.shareit.mapper.UserMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage inMemoryUserStorage;

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.getUser(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating new user: {}", userDto);
        User user = UserMapper.toUser(userDto);
        validateDuplicateEmail(user.getEmail());
        return UserMapper.toUserDto(inMemoryUserStorage.createUser(user));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Updating user: {}", userDto);
        User user = UserMapper.toUser(userDto);
        getUser(userId);
        user.setId(userId);
        validateDuplicateEmail(user.getEmail());
        return UserMapper.toUserDto(inMemoryUserStorage.updateUser(user));
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Deleting user: {}", userId);
        getUser(userId);
        inMemoryUserStorage.deleteUser(userId);
    }

    private void validateDuplicateEmail(String email) {
        if (inMemoryUserStorage.getAllUsers().stream()
                .anyMatch(user1 -> user1.getEmail().equals(email))) {
            throw new ConflictException("Email already exists");
        }
    }
}
