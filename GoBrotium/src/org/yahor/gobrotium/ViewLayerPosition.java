package org.yahor.gobrotium;

import java.io.Serializable;

public class ViewLayerPosition implements Serializable{
    private int parentIndex;
    private int childIndex;

    public ViewLayerPosition(int parentIndex, int childIndex) {
        setParentIndex(parentIndex);
        setChildIndex(childIndex);
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    public int getChildIndex() {
        return childIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ViewLayerPosition) {
            ViewLayerPosition other = (ViewLayerPosition) obj;
            return other.getParentIndex() == this.getParentIndex() && other.getChildIndex() == this.getChildIndex();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getParentIndex()+""+getChildIndex());
    }

    @Override
    public String toString() {
        return String.format("[%d : %d]", getParentIndex(), getChildIndex());
    }
}
