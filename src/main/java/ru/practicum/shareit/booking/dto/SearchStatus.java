package ru.practicum.shareit.booking.dto;

public enum SearchStatus {
    ALL, //все
    CURRENT, //текущие
    PAST, //завершённые **PAST** (англ. «завершённые»),
    FUTURE, //будущие
    WAITING, //ожидающие подтверждения
    REJECTED //отклонённые
}