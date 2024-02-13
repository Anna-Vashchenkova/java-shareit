package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.Status;
import ru.practicum.shareit.user.dto.UserMapper;

public class ItemMapper {
    public static ItemOutcomeDto toItemDto(Item item) {
        return new ItemOutcomeDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable() == Status.AVAILABLE, UserMapper.toUserDto(item.getOwner()),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }
}