package com.choi.doit.global.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCodeGenerator {
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
}
