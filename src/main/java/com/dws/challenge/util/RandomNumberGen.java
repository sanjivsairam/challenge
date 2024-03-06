package com.dws.challenge.util;

import java.util.Random;

public class RandomNumberGen {

    /**
 * Returns a random integer between 0 (inclusive) and the specified maximum value (exclusive).
 *
 * @return a random integer between 0 (inclusive) and max (exclusive)
 * @throws IllegalArgumentException if max is not positive
 */
public static int getRandomNum() {

    Random rand = new Random();
    long time = System.currentTimeMillis();
    int randNum = rand.nextInt((int) time);
    System.out.println("Random number: " + randNum);

    return randNum;
}

}