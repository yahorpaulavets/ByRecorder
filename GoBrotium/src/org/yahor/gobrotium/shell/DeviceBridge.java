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

package org.yahor.gobrotium.shell;

import com.android.ddmlib.*;
import com.android.ddmlib.log.LogReceiver;
import org.yahor.gobrotium.config.ConfigurationManager;
import org.yahor.gobrotium.utils.FileUtils;
import org.yahor.gobrotium.utils.L;

import java.io.File;
import java.io.IOException;

public class DeviceBridge {
    private AndroidDebugBridge mBridge;
    private IDevice mTargetDevice = null;
    private File outputFile = null;

    public DeviceBridge(String adbExecutable) {
        L.i("Acquiring the mBridge.");
        acquireAndroidDebugBridge(adbExecutable);
    }

    public void acquireAndroidDebugBridge(String adbExecutable) {
        AndroidDebugBridge.init(false);

        L.d("########### adb: " + adbExecutable);
        setBridge(AndroidDebugBridge.createBridge(adbExecutable, false));

        initDevice(false);
    }

    public void initDevice(boolean listDevicesOnly) {
        int count = 0;
        while (!getBridge().hasInitialDeviceList()) {
            try {
                Thread.sleep(300L);
                count++;
            } catch (InterruptedException e) {
            }

            if (count > 100) {
                System.err.println("Timeout getting hardware list!");
                return;
            }
        }

        IDevice[] devices = getBridge().getDevices();

        for (IDevice device : devices) {
            // TODO: Review
            if (!listDevicesOnly) {
                setTarget(device);
                break;
            } else if (listDevicesOnly) {
                L.i("[ List devices ] Connected hardware: " + device.getSerialNumber());
            }
        }

        if(getTargetDevice() == null) {
            L.i("Unable to connect to test device!");
//            System.exit(0);
        }
    }

    public void setTarget(IDevice device) {
        if(device == null) {
            L.i("Device is null!");
            return;
        }

        L.i("Target hardware is set to: " + device.getSerialNumber());
        ConfigurationManager.setDeviceSn(device.getSerialNumber());
        mTargetDevice = device;
    }

    public void execShell(String command, IShellOutputReceiver shellOutputReceiver) throws IOException, AdbCommandRejectedException, ShellCommandUnresponsiveException, TimeoutException {
        mTargetDevice.executeShellCommand(command, shellOutputReceiver);
    }

    public IDevice getTargetDevice() {
        if(mTargetDevice == null) {
            L.i("Target hardware is null!");
        }

        return mTargetDevice;
    }

    public void listConnectedDevices() {
        initDevice(true);
    }

    public AndroidDebugBridge getBridge() {
        return mBridge;
    }

    public void setBridge(AndroidDebugBridge mBridge) {
        this.mBridge = mBridge;
    }

    LogReceiver logReceiver = new LogReceiver(new LogReceiver.ILogListener() {
        @Override
        public void newEntry(LogReceiver.LogEntry logEntry) {
            //To change body of implemented methods use File | Settings | File Templates.
            if(outputFile == null) {
                L.i(new String(logEntry.data));
            } else {
                String logEntryAppend = new String(logEntry.data);
                FileUtils.writeText(outputFile, logEntryAppend, true);
            }
        }

        @Override
        public void newData(byte[] bytes, int i, int i2) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    });

    public void runLogReceiver(File file) {
        try {
            outputFile = file;
            this.getTargetDevice().runLogService("main", logReceiver);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void stopLogReciever() {
        try {
            this.getTargetDevice().runLogService("main", null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void runLogReceiver(File file, LogReceiver receiver) {
        try {
            outputFile = file;
            this.getTargetDevice().runLogService("main", receiver);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void runLogReceiver(LogReceiver receiver) {
        try {
            this.getTargetDevice().runLogService("main", receiver);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
