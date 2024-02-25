package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addNewRequest(Long userId, String description);

    ItemRequest getRequestById(Long userId, Long requestId);

    List<ItemRequest> getRequests(Long userId);
}
