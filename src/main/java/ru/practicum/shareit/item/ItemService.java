package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item addNewItem(Long userId, Long id, String name, String description, Boolean available);

    void deleteItem(Long userId, Long itemId);

    Item updateItem(Long userId, Long id, String name, String description, Boolean available);

    Item getItemById(Long userId, long itemId);

    List<Item> findItemsByOwnerId(Long userId);

    List<Item> searchItem(String text);

    List<Comment> getComments(Long iteId);
}
