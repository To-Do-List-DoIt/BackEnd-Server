package com.choi.doit.domain.user.dto.request;

import com.choi.doit.domain.user.domain.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class EmailJoinRequestDto {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$")
    private String email;

    @NotBlank
    @NotNull
    @Size(min = 5, max = 20)
    private String password;

    private MultipartFile profile;

    public UserEntity toEntity(String profile_image_path) {
        return new UserEntity(this, profile_image_path);
    }
}
