package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.groupvalid.CreateInfo;
import ru.practicum.shareit.groupvalid.UpdateInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank(groups = {CreateInfo.class})
    private String name;
    @NotEmpty(groups = CreateInfo.class)
    @Email(groups = {CreateInfo.class, UpdateInfo.class})
    private String email;
}
