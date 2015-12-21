/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bntu.masterscourse.ypaulavets.byrecorder.utils;

/**
 *
 * @author yahor
 */
public class ThreadUtil {

    public static void sleep(int ms) {
        try {
            if (Constants.DEBUG) {
                LoggerUtil.log("Sleeping: " + ms);
            }
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            System.out.println("Sleep is interrupted");
        }
    }
}
