package com.choi.doit.domain.user.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NicknameVo {
    @NotNull
    @Size(min = 2, max = 10)
    private String nickname;
}
