package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);
    Item addNewItem(Long userId, Item item);
    void deleteItem(Long userId, Long itemId);
}
