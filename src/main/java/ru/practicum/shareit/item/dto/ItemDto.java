package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ItemDto {
    private String name = null;
    private String description = null;
    private Boolean available = null;
    private Long request;

}
