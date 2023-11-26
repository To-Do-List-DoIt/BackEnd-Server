package com.choi.doit.global.oauth2;

import com.choi.doit.domain.model.Provider;
import com.choi.doit.domain.user.dto.OAuthUserInfoDto;
import com.choi.doit.domain.user.exception.UserErrorCode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RequiredArgsConstructor
@Component
@Slf4j
public class GoogleOAuth {
    @Value("${GOOGLE_OAUTH_CLIENT_ID}")
    private String CLIENT_ID;

    // id-token을 이용한 인증
    public OAuthUserInfoDto authenticate(String id_token) throws GeneralSecurityException, IOException {
        HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        if (id_token != null) {
            GoogleIdToken verifiedToken = verifier.verify(id_token);

            if (verifiedToken != null) {
                GoogleIdToken.Payload payload = verifiedToken.getPayload();

                // Get profile information from payload
                String email = payload.getEmail();
                String profileImagePath = (String) payload.get("picture");

                return OAuthUserInfoDto.builder()
                        .provider(Provider.GOOGLE)
                        .email(email)
                        .profile_image_path(profileImagePath)
                        .build();
            } else {
                throw new AuthenticationServiceException(UserErrorCode.INVALID_ID_TOKEN.getMessage());
            }
        }

        return null;
    }
}
