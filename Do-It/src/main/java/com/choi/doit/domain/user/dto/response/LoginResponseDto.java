package com.choi.doit.domain.user.dto.response;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    private Long user_id;
    private String access_token;
    private String refresh_token;
}
