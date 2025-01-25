package com.example.kulvida.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Locale;
import java.util.Random;

@Component
public class RandomGeneratorUtil {

    public String generateOrderId(){
        byte[] array = new byte[10]; //length of the orderId
        new Random().nextBytes(array);
        String generated= Base64.getEncoder().encodeToString(array);
        Character[] chars = generated.chars().filter(Character::isLetterOrDigit).mapToObj(c -> (char)c).toArray(Character[]::new);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            sb.append(chars[i]);
        }
        return sb.toString().toUpperCase(Locale.ROOT);
    }
}
