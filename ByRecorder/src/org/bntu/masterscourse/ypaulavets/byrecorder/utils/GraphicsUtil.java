/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bntu.masterscourse.ypaulavets.byrecorder.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author yahor
 */
public class GraphicsUtil {
 public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
}
