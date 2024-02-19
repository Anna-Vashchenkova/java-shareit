package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item addNewItem(Long userId, String name, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long id, String name, String description, Boolean available);

    Item getItemById(Long userId, long itemId);

    List<Item> findItemsByOwnerId(Long userId);

    List<Item> searchItem(String text);

    boolean userIsOwnerOfItem(long userId, Long itemId);
}
