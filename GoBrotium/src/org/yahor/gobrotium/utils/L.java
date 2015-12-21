package org.yahor.gobrotium.utils;

import org.yahor.gobrotium.config.Constants;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class L {
    public static void i(String message) {
        if(Constants.Configuration.VERBOSE_LEVEL > 0) {
            System.out.println(getCurrentTime() + " [i] "+ message);
        }
    }

    public static void e(String message) {
        if(Constants.Configuration.VERBOSE_LEVEL > 0) {
            System.out.println(getCurrentTime() + " [e] "+ message);
        }
    }

    public static void m(String message) {
        if(Constants.Configuration.VERBOSE_LEVEL > 1) {
            System.out.println(getCurrentTime() + " [m] " + message);
        }
    }

    public static void fe(String message) {
        System.out.println(getCurrentTime() + " [fatal] " + message);
    }

    public static void ii(String message) {
        System.out.println(getCurrentTime() + " [out] " + message);
    }

    public static void d(String message) {
        if(Constants.Configuration.VERBOSE_LEVEL > 2) {
            System.out.println(getCurrentTime() + " [d] " + message);
        }
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
    }

    public static String getDate(long occurred) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
        return sdf.format(occurred);
    }

    public static void a(Component parent, String s) {
        JOptionPane.showMessageDialog(parent, s);
    }
}
