package org.bntu.masterscourse.ypaulavets.byrecorder;

public class PauseHistoryItem extends TestCaseItem {
    private long pause;

    public PauseHistoryItem(long pause) {
        setPause(pause);
    }

    public void setPause(long pause) {
        this.pause = pause;
    }

    public long getPause() {
        return pause;
    }
}
