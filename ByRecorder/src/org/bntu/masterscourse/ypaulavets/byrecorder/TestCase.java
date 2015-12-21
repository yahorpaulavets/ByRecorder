package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.yahor.gobrotium.utils.L;

import java.io.Serializable;
import java.util.ArrayList;

public class TestCase implements Serializable{
    private long id;
    private ArrayList<TestCaseItem> testCaseSteps;
    private boolean enabled;

    public TestCase(boolean isEnabled) {
        setId(System.currentTimeMillis());
        initTestStepsQueue();
        setEnabled(isEnabled);
    }

    private void initTestStepsQueue() {
        this.testCaseSteps = new ArrayList<TestCaseItem>(1000);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public ArrayList<TestCaseItem> getTestCaseSteps() {
        return testCaseSteps;
    }

    public void setTestCaseSteps(ArrayList<TestCaseItem> testCaseSteps) {
        this.testCaseSteps = testCaseSteps;
    }

    public void record(ShellKeyEventHistoryItem event) {
        testCaseSteps.add(event);
    }

    public void record(TestCaseItem item) {
        testCaseSteps.add(item);
    }

    public TestCaseItem getStep(int stepIndex) {
        return testCaseSteps.get(stepIndex);
    }

    public void normalizePauses() {
        if(getTestCaseSteps() == null || getTestCaseSteps().size() < 2) {
            return;
        }

        long start = getStep(0).getOccurred();
        logTestCase(false);
        ArrayList<TestCaseItem> result = new ArrayList<TestCaseItem>(testCaseSteps.size()*2);

        result.add(testCaseSteps.get(0));
        boolean continuationOccurred = false;
        for (int i = 1; i < testCaseSteps.size(); i++) {
            TestCaseItem item = testCaseSteps.get(i);
            if(item instanceof PauseHistoryItem) {
                continuationOccurred = true;
                continue;
            }

            if(testCaseSteps.get(i-1) instanceof PauseHistoryItem) {
                continuationOccurred = true;
                continue;
            }

            if(continuationOccurred) {
                continuationOccurred = false;
                start = testCaseSteps.get(i-1).getOccurred();
            }

            long pause = item.getOccurred() - start;
            result.add(new PauseHistoryItem(Math.abs(pause)));
            result.add(item);
            start = item.getOccurred();
        }

        testCaseSteps = result;
        logTestCase(false);
    }

    public void replaceTestStep(TestCaseItem itemToBeReplaced, TestCaseItem item) {
        // todo get current test case id
        L.e("//// before replacement");
        logTestCase(true);
        for(int i = 0; i < testCaseSteps.size(); i++) {
            TestCaseItem item2 = testCaseSteps.get(i);
            if(item2.getOccurred() == itemToBeReplaced.getOccurred()) {
                testCaseSteps.set(i, item);
                if(RecordingManager.getInstance().isTestRecording()) {
                    testCaseSteps.remove(testCaseSteps.size() - 1);
                }
                break;
            }
        }

        L.e("//// after replacement");
        logTestCase(false);
    }


    private void logTestCase(boolean inout) {
        for(TestCaseItem item: testCaseSteps) {
            L.e(inout ? " <<<<<<<<<<<< " + item : ">>>>>>>>>>>> " + item);
        }
    }

    public void removeStep(TestCaseItem item) {
        testCaseSteps.remove(item);
    }

    public boolean isEmpty() {
        return  testCaseSteps == null || testCaseSteps.isEmpty();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
