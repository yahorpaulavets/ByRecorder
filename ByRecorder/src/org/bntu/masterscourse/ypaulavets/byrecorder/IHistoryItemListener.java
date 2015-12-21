package org.bntu.masterscourse.ypaulavets.byrecorder;

public interface IHistoryItemListener {
    public void onHistoryItemRecorded(long id, TestCaseItem item);
    public void onHistoryItemPlayed(long id, TestCaseItem item);
    public void onTestSuiteLoaded();
}
