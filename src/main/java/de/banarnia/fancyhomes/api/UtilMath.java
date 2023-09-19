package de.banarnia.fancyhomes.api;

/**
 * General math methods.
 */
public class UtilMath {

    /**
     * Check if a given String is an Znteger.
     * @param str String to check.
     * @return True if the String can be parsed to an Tnteger, else false.
     */
    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a given String is a Float.
     * @param str String to check.
     * @return True if the String can be parsed to a Float, else false.
     */
    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check if a given String is a Long.
     * @param str String to check.
     * @return True if the String can be parsed to a Long, else false.
     */
    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Round a double
     * @param number Number to round.
     * @param points Decimal points.
     * @return Round double.
     */
    public static double unsafeRound(double number, int points) {
        double multi = Math.pow(10, points);
        return (((int)(number * multi)) / multi);
    }

    /**
     * Round a float
     * @param number Number to round.
     * @param points Decimal points.
     * @return Round float.
     */
    public static float unsafeRound(float number, int points) {
        return Double.valueOf(unsafeRound(Double.valueOf(number),points)).floatValue();
    }

}