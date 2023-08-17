package com.choi.doit.global.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

@Component
public class RandomUtil {
    private final char[] characterTable = new char[]{
            //number
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            //uppercase
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            //lowercase
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            //special symbols
            '@', '$', '!', '%', '*', '?', '&'
    };

    public String getRandomCode(int size) throws IllegalArgumentException {
        if (size <= 0)
            throw new IllegalArgumentException("Size must be greater than 0.");

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (random.nextBoolean()) {
                stringBuilder.append((char) (random.nextInt(26) + 65));
            } else {
                stringBuilder.append(((random.nextInt(10))));
            }
        }

        return String.valueOf(stringBuilder);
    }

    public String getRandomUsername() {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return "User" + uuid;
    }

    public String getRandomPassword(int length, boolean isEncoded) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder();

        int tableLength = characterTable.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(characterTable[secureRandom.nextInt(tableLength)]);
        }

        String raw_password = stringBuilder.toString();
        return isEncoded ? encoder.encode(raw_password) : raw_password;
    }
}
