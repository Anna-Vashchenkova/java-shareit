package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingOutcomeDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemOutcomeDto item;
    private UserDto booker;
    private String status;
}
