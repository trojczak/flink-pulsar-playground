package pl.trojczak.flinkpulsar.playground.bank.util;

import java.util.Random;

public class RandomUtil {

    private static Random random = new Random();

    public static int getRandomInt(int end) {
        return random.nextInt(end);
    }

    public static int getRandomInt(int start, int end) {
        return random.nextInt(end + start) - start;
    }
}