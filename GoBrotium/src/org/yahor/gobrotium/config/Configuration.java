/**
 Authors:

 Yahor Paulavets (paulavets.pride@gmail.com)

 This file is part of Gobrotium project (https://github.com/a-a-a-CBEI-I-IEE-M9ICO/GoBrotium.git)

 Gobrotium project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gobrotium is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gobrotium project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.yahor.gobrotium.config;

import org.yahor.gobrotium.shell.DeviceBridge;
import org.yahor.gobrotium.utils.L;

import java.io.File;

public class Configuration {
    private File audioHome;
    private File vcfHome;
    private DeviceBridge deviceBridge;
    private String adbFileName;
    private OperationalSystem currentOS;
    private File adb;
    private String deviceSerialNumber;
    private String googleUserName;
    private String googleUserPassword;
    private String googleCalendarId;

    public Configuration(String androidRoot, File audioHome, File vcfFilesRoot) {
        L.i("Android root folder is: " + androidRoot);
        this.currentOS = detectOs();

        this.audioHome = audioHome;

        this.adb = new File(androidRoot, currentOS == OperationalSystem.WINDOWS
                ? Constants.PathParts.WINDOWS_ADB_EXE
                : Constants.PathParts.LINUX_ADB);

        this.vcfHome = vcfFilesRoot;

        L.i("ADB path: " + adb.getAbsolutePath() + " Exists? " + adb.exists());
    }

    public Configuration(String androidRoot) {
        L.i("Android root folder is: " + androidRoot);
        this.currentOS = detectOs();

        this.adb = new File(androidRoot, currentOS == OperationalSystem.WINDOWS
                ? Constants.PathParts.WINDOWS_ADB_EXE
                : Constants.PathParts.LINUX_ADB);

        L.i("ADB path: " + adb.getAbsolutePath() + " Exists? " + adb.exists());
    }

    public void acquireDeviceBridge() {
        this.deviceBridge = new DeviceBridge(this.adb.getAbsolutePath());
    }

    private OperationalSystem detectOs() {
        String os = System.getProperty("os.name");
        L.i("DETECTED OS: " + os);
        return os.contains("Windows") ? OperationalSystem.WINDOWS : OperationalSystem.LINUX;
    }

    public void update(Configuration config) {
        //To change body of created methods use File | Settings | File Templates.
    }

    public String getAdbExecutable() {
        return adb.getAbsolutePath();
    }

    public void setDeviceSerialNumber(String deviceSerialNumber) {
        this.deviceSerialNumber = deviceSerialNumber;
    }

    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    public DeviceBridge getDeviceBridge() {
        return this.deviceBridge;
    }

    public File getAudioHome() {
        return audioHome;
    }

    public File getVcfFilesRoot() {
        return vcfHome;
    }

    public String getGoogleUserName() {
        return googleUserName;
    }

    public void setGoogleUserName(String googleUserName) {
        this.googleUserName = googleUserName;
    }

    public String getGoogleUserPassword() {
        return googleUserPassword;
    }

    public void setGoogleUserPassword(String googleUserPassword) {
        this.googleUserPassword = googleUserPassword;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }
}
