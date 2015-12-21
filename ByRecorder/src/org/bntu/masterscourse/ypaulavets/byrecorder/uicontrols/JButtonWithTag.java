/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bntu.masterscourse.ypaulavets.byrecorder.uicontrols;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 *
 * @author yahor
 */
public class JButtonWithTag extends JButton {

    private Object mTag = null;
    private int mId = -1;

    public JButtonWithTag(String btnName, int btnId) {
        super(btnName);
        setId(btnId);
    }

    public JButtonWithTag(String btnName, int btnId, ActionListener actionListener) {
        super(btnName);
        setId(btnId);
        this.addActionListener(actionListener);
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    public Object getTag() {
        return this.mTag;
    }

    public final void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }
}
