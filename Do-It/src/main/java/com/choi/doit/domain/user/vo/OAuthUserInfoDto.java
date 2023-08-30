package com.choi.doit.domain.user.vo;

import com.choi.doit.domain.user.domain.Provider;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfoDto {
    private Provider provider;
    private String email;
    private String password;
    private String profile_image_path;
}
