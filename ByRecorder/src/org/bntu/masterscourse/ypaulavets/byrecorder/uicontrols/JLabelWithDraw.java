package org.bntu.masterscourse.ypaulavets.byrecorder.uicontrols;

import org.yahor.gobrotium.model.ViewBounds;

import javax.swing.*;
import java.awt.*;

public class JLabelWithDraw extends JLabel{
    private ViewBounds viewBounds;
    private float scaleFacrtor;

    public JLabelWithDraw(ImageIcon imageIcon) {
        super(imageIcon);
    }

    public void setNewBounds(ViewBounds viewBounds, float scaleFactor) {
        this.viewBounds = viewBounds;
        setScaleFacrtor(scaleFactor);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(viewBounds != null) {
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setColor(Color.red);
            Stroke old = graphics2D.getStroke();
            graphics2D.setStroke(new BasicStroke(3));

            int x = computeX();
            int y = computeY();
            int w = computeWidth();
            int h = computeHeight();

            graphics2D.drawRect(x,y,w,h);

            graphics2D.setStroke(old);
        }
    }

    private int computeY() {
        return (int) (viewBounds.getUpperLeftY() / scaleFacrtor);
    }

    private int computeX() {
        return (int) (viewBounds.getUpperLeftX() / scaleFacrtor);
    }

    private int computeHeight() {
        return (int) (viewBounds.getBottomRightY() / scaleFacrtor) - (int) (viewBounds.getUpperLeftY() / scaleFacrtor);
    }

    private int computeWidth() {
        return (int) (viewBounds.getBottomRightX() / scaleFacrtor) - (int) (viewBounds.getUpperLeftX() / scaleFacrtor);
    }

    public void setScaleFacrtor(float scaleFacrtor) {
        this.scaleFacrtor = scaleFacrtor;
    }

    public float getScaleFacrtor() {
        return scaleFacrtor;
    }
}
