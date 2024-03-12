package com.dws.challenge.util;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomNumberGen {

    private RandomNumberGen() {
        throw new IllegalStateException("Utility class");
    }
    /**
 * Returns a random integer between 0 (inclusive) and the specified maximum value (exclusive).
 *
 * @return a random integer between 0 (inclusive) and max (exclusive)
 * @throws IllegalArgumentException if max is not positive
 */
public static int getRandomNum() {
    Logger logger = Logger.getLogger(String.valueOf(RandomNumberGen.class));
    Random rand = new Random();
    long time = System.currentTimeMillis();
    int randNum = rand.nextInt((int) time);
    logger.log(Level.INFO,() -> "Random number: " + randNum);
    return randNum;
}

}