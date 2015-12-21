package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.yahor.gobrotium.model.AndroidKeyEvent;
import org.yahor.gobrotium.model.View;
import org.yahor.gobrotium.utils.L;

import java.io.Serializable;

public class TestCaseItem implements Serializable {
    private int x;
    private int y;
    private long occurred;
    private Object tag;
    private int a;
    private int b;
    private int c;
    private boolean isSwipe;
    private View view;
    private int rotation;
    private boolean played;
    private TestStepResult result;
    private boolean scrollableSearchFunctionStatus;


    public TestCaseItem(String str, long occurred) {
        setOccurred(occurred);
        parseEventString(str);
    }

    public TestCaseItem() {
        setOccurred(System.currentTimeMillis());
    }


    private void parseEventString(String str) {
        String[] parts = str.split("\\s");
        setA(Integer.parseInt(parts[0], 16));
        setB(Integer.parseInt(parts[1], 16));
        setC(Integer.parseInt(parts[2], 16));
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setOccurred(long occurred) {
        this.occurred = occurred;
    }

    public long getOccurred() {
        return occurred;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getB() {
        return b;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getC() {
        return c;
    }

    public String toEventString() {
        return String.format("sendevent /dev/input/event2 %s %s %s", getA(), getB(), getC());
    }

    public void setIsSwipe(boolean isSwipe) {
        this.isSwipe = isSwipe;
    }

    public boolean isSwipe() {
        return isSwipe;
    }

    public void setSwipe(boolean isSwipe) {
        this.isSwipe = isSwipe;
    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        if(this instanceof PauseHistoryItem) {
            return "pause for: " + (((PauseHistoryItem)this).getPause()/1000) + " seconds.";
        } else if(this instanceof ShellKeyEventHistoryItem) {
            return "pressed > " + AndroidKeyEvent.getNameByCode(((ShellKeyEventHistoryItem) this).getKeyCode());
        } else if(this instanceof TextHistoryItem) {
            return "entered > " +((TextHistoryItem) this).getInputText();
        } else if (this.getTag() != null) {
            TouchTestCaseItem end = (TouchTestCaseItem) this.getTag();
            int startx = getX();
            int starty = getY();
            int endx = end.getX();
            int endy = end.getY();

            return "swipe > " + String.format("From [%d;%d] to [%d;%d]", startx, starty, endx, endy);
        } else {
            return "click at > " + extractText();
        }
    }

    private String extractText() {
        View v = getView();

        if(v!=null && v.hasResourceId()) {
            return String.format("[%d]: %s", v.getIndex(), v.getViewResourceId());
        }

        if(v != null &&
                (v.getViewClass().contains("Text") ||
                        v.getViewClass().contains("Check") ||
                        v.getViewClass().contains("Button"))) {
            String text = v.getText();
            if(text == null || text.length() == 0) {
                text = v.getContentDescription();
            }

            if(text != null && text.length() > 0) { // NOTE: trying to click on text or content description of the view
                L.i("TRYING TO EXTRACT TEXT: " + text);
                return text;
            }
        } else  if (v != null) {     // NOTE: trying to click on index based approach (view class + its index on the screen
            return String.format("View: %s, index: %d", v.getViewClass(), v.getIndex());
        }

        return String.format("[%d;%d]", getX(), getY());
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public void setResult(TestStepResult result) {
        this.result = result;
    }

    public TestStepResult isPassed() {
        return result;
    }

    public void setScrollableSearchFunctionStatus(boolean scrollableSearchFunctionStatus) {
        this.scrollableSearchFunctionStatus = scrollableSearchFunctionStatus;
    }

    public boolean isScrollableSearchFunctionStatus() {
        return scrollableSearchFunctionStatus;
    }
}
