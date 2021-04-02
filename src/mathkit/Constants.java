package mathkit;

/**
 * Utility class containing mathematical constants and useful static functions
 */
public final class Constants {
    /**
     * Value for error in rounding of doubles
     */
    private static final double EPSILON = 0.0000001;

    private Constants() {

    }

    /**
     * @param a double to compare
     * @param b double to compare
     * @return if two doubles are equal
     */
    public static boolean doublesAreEqual(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    /**
     * @param a double to check
     * @return if a double is equal to 0
     */
    public static boolean doubleEqualsZero(double a) {
        return Math.abs(a) < EPSILON;
    }

}
