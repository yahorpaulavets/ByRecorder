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

package org.yahor.gobrotium.model;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.MultiLineReceiver;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import org.yahor.gobrotium.config.Configuration;
import org.yahor.gobrotium.config.Constants;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceDisplay {
    private final Configuration config;
    private int displayWidth = 0;
    private int displayHeight;
    private String currentlyFocusedApp;
    private boolean screenTurnedOff;
    private boolean screenLocked;
    private int displayDensitiy;

    public DeviceDisplay(Configuration config) {
        this.config = config;
        refreshDisplayState();
    }

    public void refreshDisplayState() {
        try {
            config.getDeviceBridge().execShell("dumpsys window", receiver);
            fetchScreenProps();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }

    MultiLineReceiver receiver = new MultiLineReceiver() {
        @Override
        public void processNewLines(String[] strings) {
            //To change body of implemented methods use File | Settings | File Templates.
            for(String str: strings) {
                if(str.contains(Constants.DumpSysWindowPrefixes.DISPLAY)) {
                    parseDisplayDimensions(str);
                } else if(str.startsWith(Constants.DumpSysWindowPrefixes.CURRENT_FOCUS)) {
                    parseCurrentFocus(str);
                } else if(str.startsWith(Constants.DumpSysWindowPrefixes.SCREEN_STATE)) {
                    parseScreenState(str);
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    MultiLineReceiver densityReceiver = new MultiLineReceiver() {
        @Override
        public void processNewLines(String[] strings) {
            if(getDisplayDensity() > 0) {
                return;
            }

            for(String str: strings) {
                setDisplayDensitiy(Integer.parseInt(str));
            }
        }

        @Override
        public boolean isCancelled() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    private void parseScreenState(String str) {
        Pattern p = Pattern.compile(Constants.DumpSysWindowPrefixes.SCREEN_STATE + "(true|false)");
        Matcher m = p.matcher(str);

        if (m.find()) {
            setScreenTurnedOff(!Boolean.parseBoolean(m.group(1)));
        }
    }

    private void fetchScreenProps() {
        try {
            config.getDeviceBridge().execShell("getprop ro.sf.lcd_density", densityReceiver);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }

    }

    private void parseDisplayDimensions(String str) {
        Pattern p = Pattern.compile("init=.*cur=(\\d+)x(\\d+)");
        Matcher m = p.matcher(str);

        if (m.find()) {
            setDisplayWidth(m.group(1));
            setDisplayHeight(m.group(2));
        }
    }

    private void parseCurrentFocus(String str) {
        Pattern p = Pattern.compile("mCurrentFocus=Window\\{(.*)");
        Matcher m = p.matcher(str);

        if (m.find()) {
            setCurrentFocus(m.group(1));
        }
    }

    public void setDisplayWidth(String displayWidth) {
        this.displayWidth = Integer.parseInt(displayWidth);
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayHeight(String displayHeight) {
        this.displayHeight = Integer.parseInt(displayHeight);
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public String getCurrentlyFocusedApp() {
        return currentlyFocusedApp;
    }

    public void setCurrentFocus(String currentFocus) {
        if (currentFocus.contains(Constants.DumpSysWindowPrefixes.KEY_GUARD_MARKER)) {
            setScreenLocked(true);
        } else {
            setScreenLocked(false);
        }
        this.currentlyFocusedApp = currentFocus;
    }

    public void setScreenTurnedOff(boolean screenTurnedOff) {
        this.screenTurnedOff = screenTurnedOff;
    }

    public boolean isScreenTurnedOff() {
        return screenTurnedOff;
    }

    public void setScreenLocked(boolean screenLocked) {
        this.screenLocked = screenLocked;
    }

    public boolean isScreenLocked() {
        return screenLocked;
    }

    public boolean isLandscape() {
        return getDisplayWidth() > getDisplayHeight();
    }

    @Override
    public String toString() {
        return String.format("FOCUS:%s;W:%s;H:%s;IS_BLACK:%s;IS_LOCKED:%s",
                getCurrentlyFocusedApp(),
                getDisplayWidth()+"",
                getDisplayHeight()+"",
                isScreenTurnedOff()+"",
                isScreenLocked()+"");
    }

    public void setDisplayDensitiy(int displayDensitiy) {
        this.displayDensitiy = displayDensitiy;
    }

    public int getDisplayDensity() {
        return this.displayDensitiy;
    }
}
