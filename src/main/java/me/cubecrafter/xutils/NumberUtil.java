package me.cubecrafter.xutils;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class NumberUtil {

    /**
     * Checks whether a string is an integer.
     * @param input the string to test
     * @return true if the string is an integer, false otherwise
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method tests whether a given chance succeeds.
     * @param chance the chance to be tested, from 0 to 100.
     * @return true if the chance succeeds, false otherwise.
     */
    public static boolean testChance(int chance) {
        if (chance <= 0) return false;
        if (chance >= 100) return true;
        return ThreadLocalRandom.current().nextInt(100) < chance;
    }

}
