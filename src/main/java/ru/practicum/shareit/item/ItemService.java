package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item addNewItem(Long userId, Long id, String name, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long id, String name, String description, Boolean available);

    Item getItemById(long itemId);

    Item searchItem(String text);
}
