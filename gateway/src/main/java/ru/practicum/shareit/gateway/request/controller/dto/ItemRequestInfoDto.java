package ru.practicum.shareit.gateway.request.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.item.controller.dto.ItemOutcomeDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestInfoDto {
    private Long id;
    @NotBlank(message = "Описание вещи не должно быть пустым")
    private String description;
    private LocalDateTime created;
    private List<ItemOutcomeDto> items;
}
