package org.bntu.masterscourse.ypaulavets.byrecorder;

import javax.swing.*;
import java.awt.*;

public class NoDeviceStub extends JFrame {
    private final Frame aFrame;

    public NoDeviceStub() {
        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        aFrame = new Frame("The cat and the fiddle");
        aFrame.add(new TextField("Please connect a device to continue"));
        aFrame.setSize(400, 100);
        aFrame.setVisible(true);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);    //To change body of overridden methods use File | Settings | File Templates.
        aFrame.setVisible(b);
    }

    @Override
    public void dispose() {
        aFrame.dispose();
        super.dispose();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
