package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.groupvalid.CreateInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = CreateInfo.class)
    private String name;
    @NotBlank(groups = CreateInfo.class)
    private String description;
    @NotNull(groups = CreateInfo.class)
    private Boolean available;
    private Long request;

}
