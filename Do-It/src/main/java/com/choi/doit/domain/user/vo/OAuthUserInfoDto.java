package com.choi.doit.domain.user.vo;

import com.choi.doit.domain.model.Provider;
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
    private String nickname;
    private String profile_image_path;
}
