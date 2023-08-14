package com.choi.doit.domain.user.dto.request;

import com.choi.doit.domain.model.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailJoinRequestDto {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$")
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 5, max = 20)
    private String password;

    @NotNull
    @Size(min = 3, max = 8)
    private String nickname;

    private MultipartFile profile;

    public UserEntity toEntity(String profile_image_path) {
        return new UserEntity(this, profile_image_path);
    }
}
