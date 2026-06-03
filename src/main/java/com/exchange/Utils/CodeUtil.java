package com.exchange.Utils;
import java.util.Random;


public class CodeUtil {

    private static final Random RANDOM = new Random();

    public static String generateSixCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}