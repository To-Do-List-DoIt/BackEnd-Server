package com.choi.doit.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DuplicateCheckRequestDto {
    @NotNull
    private String type;
    
    @NotNull
    private String value;
}
