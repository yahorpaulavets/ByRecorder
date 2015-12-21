package org.yahor.gobrotium.model;

public class Intent {
    private String action;
    private String aPackage;

    public Intent(String action) {
        setAction(action);
        setPackage("test");
    }

    private void addExtras(String key, String value) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void addExtras(String key, int value) {
        //To change body of created methods use File | Settings | File Templates.
    }

    private void addExtras(String key, boolean value) {
        //To change body of created methods use File | Settings | File Templates.
    }


    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setPackage(String aPackage) {
        this.aPackage = aPackage;
    }

    public String getaPackage() {
        return aPackage;
    }

    public void setaPackage(String aPackage) {
        this.aPackage = aPackage;
    }
}
