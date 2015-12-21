package org.bntu.masterscourse.ypaulavets.byrecorder;

public class TextHistoryItem extends TestCaseItem {
    private String inputText;

    public TextHistoryItem(String text) {
        super();
        setInputText(text);
    }

    public void setInputText(String inputText) {
        if(inputText == null) {
            return;
        }

        inputText = inputText.replaceAll("\\s", "%%s");
        this.inputText = inputText;
    }

    public String getInputText() {
        return inputText;
    }
}
