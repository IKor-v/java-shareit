package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.groupvalid.CreateInfo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemDtoIn {
    private Long id;
    @NotBlank(groups = CreateInfo.class)
    private String name;
    @NotBlank(groups = CreateInfo.class)
    private String description;
    @NotNull(groups = CreateInfo.class)
    private Boolean available;

}
