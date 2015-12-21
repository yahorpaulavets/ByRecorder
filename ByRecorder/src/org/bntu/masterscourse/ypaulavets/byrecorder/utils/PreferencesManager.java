package org.bntu.masterscourse.ypaulavets.byrecorder.utils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferencesManager {

    private Preferences mPreference;

    public PreferencesManager() {
        mPreference = Preferences.userRoot();
    }

    public boolean addSdk(String sdkPath) {
        return addValue("sdk_", sdkPath);
    }

    public boolean addTest(String testPath) {
        return addValue("test_", testPath);
    }

    private boolean addValue(String prefix, String value) {
        value = trimValue(value);

        String[] allValues;

        allValues = getAllValuesByPrefix(prefix);
        // TODO: replace with exception throw?
        if(allValues == null) return false;

        LoggerUtil.log("[value] " + value);

        if (isSdkAlreadyExists(value, allValues)) {
            LoggerUtil.log("[ADD SDK] value already exists: " + value);
            return false;
        }

        long uid = System.currentTimeMillis();

        LoggerUtil.log("[ADD value] Adding value: " + value);

        mPreference.put(prefix + uid, value);

        return true;
    }

    public String[] getAllSdks() {
        return getAllValuesByPrefix("sdk_");
    }

    public String[] getAllTests() {
        return getAllValuesByPrefix("test_");
    }

    private String[] getAllValuesByPrefix(String prefix) {
        String[] allKeys = getAllKeysByPrefix(prefix);

        if(allKeys == null) return null;

        ArrayList<String> values = new ArrayList<String>(allKeys.length);

        for (String key : allKeys) {
            String value = mPreference.get(key, null);
            LoggerUtil.log("[getAllValuesByPrefix] Found key: " + key + ": " + value);
            values.add(value.trim());
        }

        return values.toArray(new String[values.size()]);
    }

    private boolean isSdkAlreadyExists(String sdkPath, String[] allSdks) {
        return isValueAlreadyExists(sdkPath, allSdks);
    }

    private boolean isValueAlreadyExists(String value, String[] allValues) {
        value = trimValue(value);

        if (allValues == null || value == null) {
            return false;
        }

        for (String sdk : allValues) {
            LoggerUtil.log("value: " + sdk);
            if (sdk == null) {
                continue;
            }
            if (sdk.equals(value)) {
                LoggerUtil.log("Found existing value for: " + value);
                return true;
            }
        }

        LoggerUtil.log("Unable to find existing value for: " + value);

        return false;
    }

    public void removeSdk(String sdkPath) {
        sdkPath = trimValue(sdkPath);

        removeKeyByValue("sdk_", sdkPath);
    }

    public void removeTest(String testPath) {
        testPath = trimValue(testPath);

        removeKeyByValue("test_", testPath);
    }

    private void removeKeyByValue(String prefix, String valueToRemove) {
        String[] allKeys = getAllKeysByPrefix(prefix);

        if(allKeys == null) return;

        ArrayList<String> sdks = new ArrayList<String>(allKeys.length);

        for (String key : allKeys) {
            String value = mPreference.get(key, null);

            if (value.equals(valueToRemove)) {
                LoggerUtil.log("[PURGE] Found key: " + key + ": " + value);
                mPreference.remove(key);
                return;
            }
        }

        LoggerUtil.log("[REMOVE] NOT FOUND: " + valueToRemove);
    }

    public void setDefaultSdk(String sdkPath) {
        sdkPath = trimValue(sdkPath);
        mPreference.put("default_sdk", sdkPath);
        LoggerUtil.log("[NEW DEFAULT SDK] SDK: " + sdkPath);
    }

    public String getDefaultSdk() {
        String sdk = mPreference.get("default_sdk", null);
        sdk = trimValue(sdk);
        return sdk;
    }

    private String trimValue(String sdkPath) throws NullPointerException {
        if (StringUtils.isNullOrEmpty(sdkPath)) {
            LoggerUtil.log("[TRIM] Wrong adbLocation passed.");
            return null;
        }

        sdkPath = sdkPath.trim();

        return sdkPath;
    }

    public void clear() {
        try {
            mPreference.clear();
        } catch (BackingStoreException ex) {
            Logger.getLogger(PreferencesManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeAllTests() {
        String[] allTests = getAllKeysByPrefix("test_");

        if(allTests == null) return;

        for(String test: allTests) {
            mPreference.remove(test);
        }
    }

    public String[] getAllKeysByPrefix(String prefix) {
        String[] allKeys = null;
        try {
            allKeys = mPreference.keys();
        } catch (BackingStoreException ex) {
            LoggerUtil.log("Unable to get keys for: " + prefix);
            return null;
        }
        ArrayList<String> keysByPrefix = new ArrayList<String>(allKeys.length);
        for(String key: allKeys) {
            if(!key.startsWith(prefix)) {
                continue;
            }

            keysByPrefix.add(key);
        }

        return keysByPrefix.toArray(new String[keysByPrefix.size()]);
    }
}
