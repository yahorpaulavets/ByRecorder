package org.yahor.gobrotium.utils;

public class StringUtils {
    public static boolean isNullOrEmpty(String deviceSn) {
        return deviceSn == null || deviceSn.length() == 0;
    }
}
