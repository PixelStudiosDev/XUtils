package dev.pixelstudios.xutils;

import lombok.experimental.UtilityClass;

import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class NumberUtil {

    private final static TreeMap<Integer, String> ROMAN_NUMERALS = new TreeMap<>();

    static {
        ROMAN_NUMERALS.put(1000, "M");
        ROMAN_NUMERALS.put(900, "CM");
        ROMAN_NUMERALS.put(500, "D");
        ROMAN_NUMERALS.put(400, "CD");
        ROMAN_NUMERALS.put(100, "C");
        ROMAN_NUMERALS.put(90, "XC");
        ROMAN_NUMERALS.put(50, "L");
        ROMAN_NUMERALS.put(40, "XL");
        ROMAN_NUMERALS.put(10, "X");
        ROMAN_NUMERALS.put(9, "IX");
        ROMAN_NUMERALS.put(5, "V");
        ROMAN_NUMERALS.put(4, "IV");
        ROMAN_NUMERALS.put(1, "I");
    }

    public static String toRoman(int number) {
        int l = ROMAN_NUMERALS.floorKey(number);

        if (number == l) {
            return ROMAN_NUMERALS.get(number);
        }

        return ROMAN_NUMERALS.get(l) + toRoman(number - l);
    }
    
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
     * @param chancePercent the chance percentage (0-100%)
     * @return true if the chance succeeds, false otherwise.
     */
    public static boolean testChance(int chancePercent) {
        if (chancePercent <= 0) return false;
        if (chancePercent >= 100) return true;

        return ThreadLocalRandom.current().nextInt(100) < chancePercent;
    }

    public static String format(int number, char separator) {
        return String.format("%,d", number).replace(',', separator);
    }

    public static String commas(int number) {
        return String.format("%,d", number);
    }

    public static int getPercentage(int value, int max) {
        if (max == 0) return 0;

        return NumberUtil.clamp(
                (int) ((double) value / max * 100),
                0, 100
        );
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
