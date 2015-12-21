package org.bntu.masterscourse.ypaulavets.byrecorder;

public class ShellKeyEventHistoryItem extends TestCaseItem {
    private int keyCode;

    public ShellKeyEventHistoryItem(int keyCode) {
        setKeyCode(keyCode);
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
