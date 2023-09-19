package de.banarnia.fancyhomes.api;

/**
 * Methods for version checking.
 */
public class UtilVersion {

    /**
     * Checks if a given String is a valid version String.
     * @param version Version to check.
     * @return True if the String is a valid version, else false.
     */
    public static boolean isValidVersion(String version) {
        // Null check.
        if (version == null || version.isEmpty())
            return false;

        // Split into arrays that should be integers -> 1.1.0.
        String[] versionArr = version.split("\\.");

        // Try to parse into integers.
        for (String split : versionArr)
            if (!UtilMath.isInt(split))
                return false;

        return true;
    }

    /**
     * Returns the single ints of a version String in an array.
     * @param version Version to check.
     * @return Array of ints.
     */
    public static int[] getVersionAsIntArray(String version) {
        // Check if input is valid.
        if (!isValidVersion(version))
            return null;

        // Split version.
        String[] versionArr = version.split("\\.");

        // int array.
        int[] versionIntArray = new int[versionArr.length];

        // Parse ints.
        for (int i = 0; i < versionArr.length; i++)
            versionIntArray[i] = Integer.parseInt(versionArr[i]);

        return versionIntArray;
    }

    /**
     * Checks if a given version is lower than another one.
     * @param versionToCompare Version that may be lower.
     * @param baseVersion Base version to compare to.
     * @return True if versionToCompare is lower than baseVersion.
     */
    public static boolean isLower(String versionToCompare, String baseVersion) {
        // Check if versionToCompare is valid.
        if (!isValidVersion(versionToCompare))
            return true;

        // Check if baseVersion is valid. If not return false.
        if (!isValidVersion(baseVersion))
            return false;

        // Get versions as int arrays.
        int[] versionToCompareIntArr = getVersionAsIntArray(versionToCompare);
        int[] baseVersionIntArr = getVersionAsIntArray(baseVersion);

        // Compare ints.
        for (int i = 0; i < versionToCompareIntArr.length; i++) {
            // Get int to compare.
            int toCompare = versionToCompareIntArr[i];

            // Check if other version is shorter.
            if (i >= baseVersionIntArr.length)
                return false;

            // Compare ints.
            int base = baseVersionIntArr[i];

            if (toCompare < base)
                return true;
            else if (toCompare > base)
                return false;

            // Check if other version is longer.
            if (i == versionToCompareIntArr.length - 1 && baseVersionIntArr.length > versionToCompareIntArr.length)
                for (int j = i + 1; j < baseVersionIntArr.length; j++)
                    if (baseVersionIntArr[j] > 0)
                        return true;
        }

        return false;
    }

}