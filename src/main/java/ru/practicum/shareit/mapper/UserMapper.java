package ru.practicum.shareit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
