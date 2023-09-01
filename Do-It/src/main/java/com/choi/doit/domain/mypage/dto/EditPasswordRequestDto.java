package com.choi.doit.domain.mypage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EditPasswordRequestDto {
    @NotBlank
    @NotNull
    @Size(min = 5, max = 20)
    private String password;
}
