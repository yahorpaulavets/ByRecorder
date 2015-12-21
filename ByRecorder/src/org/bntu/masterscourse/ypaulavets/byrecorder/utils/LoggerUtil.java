/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bntu.masterscourse.ypaulavets.byrecorder.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author yahor
 */
public class LoggerUtil {

    public static void log(String message) {

        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        String dateFormatted = formatter.format(date);

        System.out.println(dateFormatted + "> " + message);
    }
}
