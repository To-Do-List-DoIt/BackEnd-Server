package com.choi.doit.global.util;

import com.choi.doit.global.error.GlobalErrorCode;
import com.choi.doit.global.error.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class ImageHandler {
    @Value("${PROFILE_DATABASE_URL}")
    private String DIR_PATH;

    // 프로필 이미지 저장
    public String saveProfileImage(String email, MultipartFile image) throws IOException, RestApiException {
        // 파일이 이미지가 아닐 경우
        if (!Objects.requireNonNull(image.getContentType()).split("/")[0].equals("image"))
            throw new RestApiException(GlobalErrorCode.INVALID_FILE_FORMAT);

        String file_url = "/" + email + ".jpeg";
        String file_path = DIR_PATH + file_url;
        image.transferTo(new File(file_path));

        log.info("File uploaded: " + file_url);

        return file_url;
    }

    // 프로필 이미지 삭제
    public void deleteProfileImage(String file_url) throws RestApiException {
        File file = new File(DIR_PATH + file_url);
        boolean result = file.delete();

        // 결과
        if (result) {
            log.info("File deleted: " + file_url);
        } else {
            log.error("File deletion failed: " + file_url);
            throw new RestApiException(GlobalErrorCode.FILE_DELETION_FAILED);
        }
    }
}
