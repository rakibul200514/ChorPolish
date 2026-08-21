package com.rakibul.chorpolish.utils;

public class VersionUtils {

    /**
     * Compares two version strings.
     *
     * @param version1 the first version string
     * @param version2 the second version string
     * @return 0 if the versions are equal, 1 if version1 is greater than version2,
     *         -1 if version1 is less than version2
     */
    public static int compareVersions(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return 0;
        }

        String[] levels1 = version1.split("\\.");
        String[] levels2 = version2.split("\\.");

        int length = Math.max(levels1.length, levels2.length);
        for (int i = 0; i < length; i++) {
            int v1 = i < levels1.length ? Integer.parseInt(levels1[i]) : 0;
            int v2 = i < levels2.length ? Integer.parseInt(levels2[i]) : 0;

            if (v1 > v2) {
                return 1;
            }
            if (v1 < v2) {
                return -1;
            }
        }

        return 0;
    }
}
