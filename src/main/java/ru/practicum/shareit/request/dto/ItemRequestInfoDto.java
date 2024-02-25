package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemOutcomeDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestInfoDto {
    private Long id;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    private LocalDateTime created;
    private List<ItemOutcomeDto> itemsDto;
}
