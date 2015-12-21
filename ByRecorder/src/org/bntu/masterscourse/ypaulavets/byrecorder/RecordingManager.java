package org.bntu.masterscourse.ypaulavets.byrecorder;

import java.io.*;
import java.util.HashMap;

public class RecordingManager implements Serializable{
    private static RecordingManager instance;
//    private IHistoryItemListener testRecordingListener;
    private boolean testPlaying;
    private boolean testRecording;
    private HashMap<Long, TestCase> currentTestSuite;
    private TestCase currentTestCase;

    private RecordingManager() {
        startHistory();
    }

    private void startHistory() {
        this.currentTestSuite = new HashMap<Long, TestCase>(100);
    }

    public void serialize(String absolutePath) {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(absolutePath);
            out = new ObjectOutputStream(fos);
            out.writeObject(currentTestSuite);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deserialize(String absolutePath) {
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(absolutePath);
            in = new ObjectInputStream(fis);
            this.currentTestSuite = (HashMap<Long, TestCase>) in.readObject();
            if(currentTestSuite.size() > 0 && currentTestSuite.keySet().iterator().hasNext()) {
                long id = currentTestSuite.keySet().iterator().next();
                currentTestCase = currentTestSuite.get(id);
            }
            in.close();

            JTestRecordingFrame.getInstance().onTestSuiteLoaded();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void normalizeHistory() {
        // todo get current test case id

        currentTestCase.normalizePauses();
    }

    public static RecordingManager getInstance() {
        if(instance == null) {
            instance = new RecordingManager();
        }

        return instance;
    }

    public void removeItem(long testCaseId, TestCaseItem item) {
        currentTestSuite.get(testCaseId).removeStep(item);
    }

    public boolean isTestPlaying() {
        return testPlaying;
    }

    public void setTestPlaying(boolean testPlaying) {
        this.testPlaying = testPlaying;
    }

    public boolean isTestRecording() {
        return testRecording;
    }

    public void setTestRecording(boolean testRecording) {
        this.testRecording = testRecording;
    }

    public void replaceItem(TestCaseItem itemToBeReplaced, TestCaseItem item) {
       currentTestCase.replaceTestStep(itemToBeReplaced, item);
    }

    public Object getLoadedTestCasesCount() {
        return currentTestSuite.size();
    }

    public void startNewTestCase() {
        currentTestCase = new TestCase(true);
        currentTestSuite.put(currentTestCase.getId(), currentTestCase);
    }

    public void record(ShellKeyEventHistoryItem event) {
        currentTestCase.record(event);
        JTestRecordingFrame.getInstance().onHistoryItemRecorded(currentTestCase.getId(), event);
    }

    public void record(String text) {
        if(text != null && text.length() > 0) {
            TextHistoryItem item = new TextHistoryItem(text);
            currentTestCase.record(item);
            JTestRecordingFrame.getInstance().onHistoryItemRecorded(currentTestCase.getId(), item);
        }
    }

    public void record(TouchTestCaseItem item) {
        currentTestCase.record(item);
        JTestRecordingFrame.getInstance().onHistoryItemRecorded(currentTestCase.getId(), item);
    }

    public TestCase getCurrentTestCase() {
        return currentTestCase;
    }

    public void setCurrentTestCase(TestCase currentTestCase) {
        this.currentTestCase = currentTestCase;
    }

    public TestCase getTestCase(long testCaseId) {
        return currentTestSuite.get(testCaseId);
    }

    public HashMap<Long, TestCase> getCurrentTestSuite() {
        return currentTestSuite;
    }

    public void removeTestCase(long id) {
        TestCase tc = currentTestSuite.get(id);
        if(tc == null) return;

        currentTestSuite.remove(id);
    }
}
