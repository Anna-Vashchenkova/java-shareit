package ru.practicum.shareit.user;

import ru.practicum.shareit.item.dto.ItemDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
