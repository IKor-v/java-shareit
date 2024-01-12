package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.groupvalid.CreateInfo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank(groups = {CreateInfo.class})
    private String name;
    @Email(groups = {CreateInfo.class})
    private String email;
}
