package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.yahor.gobrotium.model.View;

import java.io.Serializable;

public class TouchTestCaseItem extends TestCaseItem implements Serializable {

    public TouchTestCaseItem(int rotation, View v, int x, int y, long tag) {
        this(x, y, tag);
        setView(v);
        setRotation(rotation);
    }

    public TouchTestCaseItem(int x, int y, long occurred) {
        setX(x);
        setY(y);
        setOccurred(occurred);
        setPlayed(false);
    }

    public TouchTestCaseItem(int x, int y, long occurred, Object tag) {
        this(x, y, occurred);

        setTag(tag);
    }

    public TouchTestCaseItem(int x, int y, long occurred, Object tag, boolean isSwipe) {
        this(x, y, occurred);
        setIsSwipe(isSwipe);
        setTag(tag);
    }
}
