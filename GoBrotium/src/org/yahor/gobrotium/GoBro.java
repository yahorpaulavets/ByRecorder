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

package org.yahor.gobrotium;

import com.android.ddmlib.*;
import com.android.ddmlib.log.LogReceiver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.yahor.gobrotium.config.Configuration;
import org.yahor.gobrotium.config.Constants;
import org.yahor.gobrotium.model.AndroidKeyEvent;
import org.yahor.gobrotium.model.DeviceDisplay;
import org.yahor.gobrotium.model.Intent;
import org.yahor.gobrotium.model.View;
import org.yahor.gobrotium.shell.ShellManager;
import org.yahor.gobrotium.utils.L;
import org.yahor.gobrotium.utils.StringUtils;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GoBro {
    private final Configuration config;
    private final DeviceDisplay deviceDisplay;
    private ArrayList<View> views;
    private Thread logThread;
    private Thread micStateThread;
    private int lastRotationIndex;
    private IShellOutputReceiver mEventsReceiver;
    private Document doc;

    public static final int version = 100000;

    static {
        L.ii("VERSION: " + version);
    }

    public GoBro(Configuration configuration) {
        this.config = configuration;
        this.deviceDisplay = new DeviceDisplay(this.config);
        // TODO: test timeout to avoid DdmPreferences.setTimeOut(300000);
        DdmPreferences.setTimeOut(300000);
    }

    public void inputKeyEvent(int androidKeyEventCode) {
        try {
            config.getDeviceBridge().execShell(String.format("input keyevent %d", androidKeyEventCode), reciever);
        } catch (IOException e) {
            e.printStackTrace();  //To change bod+yyuy5hty of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }

    public void inputText(String text) {
        try {
            if(text.contains(" ")) {
                text = text.replaceAll("\\s", "%s");
            }

            config.getDeviceBridge().execShell(String.format("input text %s", text), reciever);
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

    public boolean clickOnContentDescription(String text) {
        return clickOnContentDescription(text, true, true);
    }

    public boolean clickOnContentDescription(String text, boolean forceUpdateViews, boolean trueIfFullMatchNeeded) {
        if(forceUpdateViews) updateViews();

        View foundView = null;

        for(View v: views) {
            if(v.getContentDescription() == null || v.getContentDescription().length() == 0) continue;

            String contentDescription = v.getContentDescription();

            if(StringUtils.isNullOrEmpty(contentDescription)) {
                continue;
            }

            if(trueIfFullMatchNeeded && contentDescription.equalsIgnoreCase(text)) {
                foundView = v;
                break;
            } else if(!trueIfFullMatchNeeded && contentDescription.contains(text)) {
                foundView = v;
                break;
            }
        }

        if(foundView == null) {
            // No view was found
            L.i("View was not found :(");
            return false;
        }

//        L.i("Trying to tap! :)");
        tap(foundView.getBounds().getUpperLeftX()+5, foundView.getBounds().getUpperLeftY()+5);
        return true;
    }
//
//    public boolean clickOnResourceId(String id) {
//        return clickOnResourceId(id, true);
//    }
//
//    private boolean clickOnResourceId(String id, boolean forceUpdateViews) {
//        if(forceUpdateViews) updateViews();
//
//        for(View v: views) {
//            if(v.getViewResourceId() != null && v.getViewResourceId().equalsIgnoreCase(id)) {
//                return clickOnView(v);
//            }
//        }
//
//        return false;
//    }

    public boolean clickOnText(String text) {
        return clickOnText(text, true, true);
    }

    public boolean clickOnText(String text, boolean forceUpdateViews, boolean trueIfFullMatchNeeded) {
        if(forceUpdateViews) updateViews();

        View foundView = null;

        for(View v: views) {
            if(v.getText() == null || v.getText().length() == 0) continue;

            String viewText = v.getText();

            if(trueIfFullMatchNeeded && viewText.equalsIgnoreCase(text)) {
                foundView = v;
                break;
            } else if(!trueIfFullMatchNeeded && viewText.contains(text)) {
                foundView = v;
                break;
            }
        }

        if(foundView == null) {
            // No view was found
            L.i("View was not found :(");
            return false;
        }

        L.i("Trying to tap! :)");
        tap(foundView.getBounds().getUpperLeftX()+5, foundView.getBounds().getUpperLeftY()+5);
        return true;
    }

    public void tap(int x, int y) {
        try {
            config.getDeviceBridge().execShell(String.format("input tap %d %d", x, y), reciever);
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

    public void swipe(int xStart, int yStart, int xEnd, int yEnd) {
        try {
            config.getDeviceBridge().execShell(String.format("input swipe %d %d %d %d", xStart, yStart, xEnd, yEnd), reciever);
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

    public ArrayList<View> getCurrentTextViews() {
        return getCurrentViews(Constants.View.TEXT_VIEW, true);
    }

    public ArrayList<View> getCurrentTextViews(boolean forceUpdateViews) {
        return getCurrentViews(Constants.View.TEXT_VIEW, forceUpdateViews);
    }

    public int getViewsCount() {
        return getViewsCount(true);
    }

    public int getViewsCount(boolean forceUpdateViews) {
        if(forceUpdateViews) updateViews();

        return views.size();
    }

    int mLayersCounter = 0;
    LinkedHashMap<String, Integer> viewClassInstances = new LinkedHashMap<String, Integer>(100);

    public void updateViews() {
        try {
            config.getDeviceBridge().execShell("uiautomator dump", reciever);

            if (views == null) {
                views = new ArrayList<View>(150);
            } else {
                views.clear(); // = new ArrayList<View>(150);    // Suggest better value for initialization
                viewClassInstances.clear();
            }

            Long start = System.currentTimeMillis();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            ShellManager.executeAdbCommand("pull " + filePath + " .", this.config.getAdbExecutable(), this.config.getDeviceSerialNumber());

            InputStreamReader in = new InputStreamReader(new FileInputStream( "./window_dump.xml" ), "UTF8" );
            BufferedReader reader = new BufferedReader( in ); // CHANGED
            InputSource input = new InputSource(reader);

            doc = builder.parse(input);

            Node n = doc.getDocumentElement();

            if (n.getNodeName().equalsIgnoreCase("hierarchy")) {
                try {
                    String rotation = n.getAttributes().getNamedItem("rotation").getTextContent();
                    if (rotation != null) {
                        setLastRotationIndex(rotation);
                    }
                } catch (NullPointerException ex) {
                    L.e("no rotation node");
                }
            }
            mLayersCounter = 0;
            traverse(n, null);
            L.d("Processing time: " + (System.currentTimeMillis() - start));

            for(String key: viewClassInstances.keySet()) {
                Integer count = viewClassInstances.get(key);
                L.d(String.format("%s : %d instances", key, count));
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }

    ViewLayerPosition lastParent = new ViewLayerPosition(0,0);

    private void traverse(Node documentElement, View root) {
        if (documentElement.getChildNodes() == null || documentElement.getChildNodes().getLength() == 0) {
            L.d("NO CHILD NODES: " + documentElement.getNodeName());
            return;
        }

        NodeList list = documentElement.getChildNodes();

        for (int nodeIndex = 0; nodeIndex < list.getLength(); nodeIndex++) {
            Node n = list.item(nodeIndex);
            if (n.getChildNodes() == null || n.getChildNodes().getLength() == 0) {
                if (n.getAttributes() == null) {
                    L.i("NODE HAS NO ATTRS");
                } else {
                    root.addChild(addView(n, mLayersCounter, nodeIndex, root, false));
                }
            } else {
                // TODO: added 07.08.2014 attempting to get ViewGroup containers
                View parent = addView(n, mLayersCounter, nodeIndex, root, true);
                if(root != null) {
                    root.addChild(parent);
                }
                mLayersCounter++;
                traverse(n, parent);
            }
        }
        mLayersCounter--;
    }

    private View addView(Node node, int mLayersCounter, int nodeIndex, View root, boolean isParentNode) {
        View v = new View(node);
        ViewLayerPosition viewLayerPosition = new ViewLayerPosition(mLayersCounter, nodeIndex);
        v.setViewLayerPosition(viewLayerPosition);

        Integer instancesCount = viewClassInstances.get(v.getViewClass());

        if (instancesCount == null) {
            viewClassInstances.put(v.getViewClass(), 1);
            v.setInstanceIndex(0);
        } else {
            v.setInstanceIndex(instancesCount);
            instancesCount++;
            viewClassInstances.put(v.getViewClass(), instancesCount);
        }

        if (isParentNode) lastParent = v.getViewLayerPosition();

        if (root != null) {
            v.setParentView(root);
        }

        views.add(v);

        L.d(String.format("%s [%d:%d:%d] cls: (%s) rid: (%s)  txt: (%s) <- %s",
                addSpace(mLayersCounter),
                mLayersCounter,
                nodeIndex,
                v.getInstanceIndex(),
                v.getViewClass(),
                v.hasResourceId() ? v.getViewResourceId() : " no resource id",
                (v.getText().isEmpty() ? "{ " + v.getContentDescription() + " }" : v.getText()),
                v.getParentView() != null ? v.getParentView().getViewLayerPosition().toString() : "no parent"
        ));

        return v;
    }

    private String addSpace(int counter) {
        String res = "";

        for(int i = 0; i < counter; i++) {
            res+="\t";
        }

        return res;
    }

    private String filePath;
    //    private String xmlHierarchy;
    MultiLineReceiver reciever = new MultiLineReceiver() {
        @Override
        public void processNewLines(String[] strings) {
            //To change body of implemented methods use File | Settings | File Templates.
            for(String str: strings) {
                if(str.startsWith(Constants.SHELL_OUTPUT.HIERARCHY_IS_DUMPED_PATH)) {
                    filePath = str.substring(Constants.SHELL_OUTPUT.HIERARCHY_IS_DUMPED_PATH.length()+1).trim();
                    break;
                }

//                else if(str.startsWith("<?xml")) {
//                    xmlHierarchy = str;
//                }
            }
        }

        @Override
        public boolean isCancelled() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    public void sleep(float timeoutSeconds) {
        try {
            Thread.sleep((long) (timeoutSeconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public ArrayList<View> getViews() {
        return getViews(true);
    }

    public ArrayList<View> getViews(boolean forceUpdateViews) {
        if(forceUpdateViews) { updateViews(); }
        return views;
    }

    public void takeScreenshot(int rotateTimes, String filepath) throws IOException, TimeoutException, AdbCommandRejectedException {
        RawImage rawImage = config.getDeviceBridge().getTargetDevice().getScreenshot();
        if (rawImage == null) {
            return;
        }
        for(int i = 0; i < rotateTimes; i++) {
            rawImage = rawImage.getRotated();
        }

        BufferedImage image = new BufferedImage(rawImage.width, rawImage.height, 2);

        int index = 0;
        int IndexInc = rawImage.bpp >> 3;
        for (int y = 0; y < rawImage.height; y++) {
            for (int x = 0; x < rawImage.width; x++) {
                int value = rawImage.getARGB(index);
                index += IndexInc;
                image.setRGB(x, y, value);
            }
        }

        if (!ImageIO.write(image, "png", new File(filepath)))
            throw new IOException("Failed to find png writer");
    }

    public ArrayList<View> getCurrentViews(String androidViewClass) {
        return getCurrentViews(androidViewClass, true);
    }

    public ArrayList<View> getCurrentViews(String androidViewClass, boolean forceUpdateViews) {
        if(forceUpdateViews) updateViews();

        ArrayList<View> result = new ArrayList<View>(views.size());

        for(View v: views) {
            if(v.getViewClass().equalsIgnoreCase(androidViewClass)) {
                result.add(v);
            }
        }

        return result;
    }

    public boolean clickOnViewInstance(String androidViewClass, int viewInstanceIndex) {
        return clickOnViewInstance(androidViewClass, viewInstanceIndex, true);
    }

    public boolean clickOnViewInstance(String androidViewClass, int viewInstanceIndex, boolean forceUpdateViews) {
        ArrayList<View> viewsSet = getCurrentViews(androidViewClass, forceUpdateViews);

        for (int i = 0; i < viewsSet.size(); i++) {
            View v = viewsSet.get(i);
            if(i == viewInstanceIndex) {
                return clickOnView(v);
            }
        }

        return false;
    }

    public boolean clickOnView(View v) {
        if(v.isEnabled()) {
            tap(v.getViewCenterXy());
            return true;
        }

        return false;
    }

    private void tap(Point viewCenterXy) {
        tap(viewCenterXy.x, viewCenterXy.y);
    }

    public void fireIntent(Intent intent) {
//        L.i("ACTION:" + intent.getAction());
//        L.i("PACKAGE:" + intent.getPackage());
//
//        for(String key: intent.getExtras().keySet()) {
//            L.i("KEY:" + key);
//        }
    }

    public void stopLogReceiver() {
        if ( logThread != null ) {
            logThread.interrupt();
        }
    }

    public void stopMicStateLogReceiver() {
        if ( micStateThread != null ) {
            micStateThread.interrupt();
        }
    }

    public void runLogReceiver(final File file, final LogReceiver receiver) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                config.getDeviceBridge().runLogReceiver(file, receiver);
            }
        };

        logThread = new Thread(r);
        logThread.start();
    }

    public void runLogReceiver(final File file) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                config.getDeviceBridge().runLogReceiver(file);
            }
        };

        logThread = new Thread(r);
        logThread.start();
    }

    public void runLogReceiverForMicState(final LogReceiver receiver) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                config.getDeviceBridge().runLogReceiver(receiver);
            }
        };

        micStateThread = new Thread(r);
        micStateThread.start();
    }

    public boolean searchContentDescription(String text) {
        return searchContentDescription(text, true, true);
    }

    public boolean searchContentDescription(String text, boolean contains) {
        return searchContentDescription(text, true, contains);
    }

    public boolean searchContentDescription(String text, boolean forceUpdateViews, boolean contains) {
        if(forceUpdateViews) updateViews();

        ArrayList<View> views = getViews(false);

        for(View v: views) {
            if(contains && v.getContentDescription().contains(text)) {
                return true;
            }

            if(v.getContentDescription().equalsIgnoreCase(text)) {
                return true;
            }
        }

        return false;
    }

    public boolean searchText(String text, boolean forceUpdateViews, boolean contains) {
        ArrayList<View> views = getCurrentTextViews(forceUpdateViews);

        for(View v: views) {
            if(contains && v.getText().contains(text)) {
                return true;
            }

            if(v.getText().equalsIgnoreCase(text)) {
                return true;
            }
        }

        return false;
    }

    public void pressStartPlayer() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_HEADSETHOOK);
    }

    public void pressOpenSearch() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_SEARCH);
    }

    public void pressOpenPhone() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_CALL);
    }

    public void pressEndCall() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_END_CALL);
    }

    public void pressVolumeUp() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_VOLUME_UP);
    }

    public void pressVolumeDown() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_VOLUME_DOWN);
    }

    public void pressOpenBrowser() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_EXPLORER);
    }

    public void pressPowerButton() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_POWER);
    }

    public void pressMenuButton() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_MENU);
    }

    public void pressHomeButton() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_HOME);
    }

    public void pressBackButton() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_BACK);
    }

    public void swipeRight() {
        int rightEdge = this.deviceDisplay.getDisplayWidth() - 10;
        int centerY = this.deviceDisplay.getDisplayHeight()/2;

        int toX = 0;
        int toY = centerY;

        swipe(rightEdge, centerY, toX, toY);
    }

    public void execShell(String shellCommand) {
        try {
            config.getDeviceBridge().execShell(shellCommand, reciever);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentFocus() {
        deviceDisplay.refreshDisplayState();
        return deviceDisplay.getCurrentlyFocusedApp();
    }

    public DeviceDisplay getDisplayState() {
        deviceDisplay.refreshDisplayState();
        return deviceDisplay;
    }

    public void pressEnter() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_ENTER);
    }

    public void pressDelButton() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DEL);
    }

    public void printViewsToLog() {
        for(View v: getViews(true)) {
            L.i("VIEW: " + v);
        }
    }

    public void execShell(String shellCommand, IShellOutputReceiver multiLineReceiver) {
        try {
            config.getDeviceBridge().execShell(shellCommand, multiLineReceiver);
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

    public void clearLogcat() {
        try {
            config.getDeviceBridge().execShell("logcat -c", new MultiLineReceiver() {
                @Override
                public void processNewLines(String[] strings) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public boolean isCancelled() {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
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

    public void clickOnXY(int x, int y) {
        L.i("Touch: " + x + " " + y);
        tap(x,y);
    }

    public void dumpViews(String outputFileLocation) {
        try {
            config.getDeviceBridge().execShell("uiautomator dump /sdcard/" + outputFileLocation, new MultiLineReceiver() {
                @Override
                public void processNewLines(String[] strings) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public boolean isCancelled() {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
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

    public String listDevices() {
        return ShellManager.executeAdbCommand("devices", config.getAdbExecutable(), config.getDeviceSerialNumber());
    }

    public void adbPull(String filePath, String fileOutputPath) {
        ShellManager.executeAdbCommand(String.format("pull %s %s", filePath, fileOutputPath), config.getAdbExecutable(), config.getDeviceSerialNumber());
    }

    public void adbPush(String filePath, String fileOutputPath) {
        ShellManager.executeAdbCommand(String.format("push %s %s", filePath, fileOutputPath), config.getAdbExecutable(), config.getDeviceSerialNumber());
    }

    public void setLastRotationIndex(String lastRotationIndex) {
        this.lastRotationIndex = Integer.parseInt(lastRotationIndex);
    }

    public int getLastRotationIndex() {
        return lastRotationIndex;
    }

    public void pressDpadDown() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DPAD_DOWN);
    }

    public void pressDpadRight() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DPAD_RIGHT);
    }

    public void pressDpadLeft() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DPAD_LEFT);
    }

    public void pressDpadUp() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DPAD_UP);
    }

    public void pressDpadCenter() {
        inputKeyEvent(AndroidKeyEvent.KEY_CODE_DPAD_CENTER);
    }

    public void startListenEvents(IShellOutputReceiver receiver) {
        mEventsReceiver = receiver;
        execShell("uiautomator events", receiver);
    }

    public void stopListenEvents() {
        if(mEventsReceiver instanceof MultiLineReceiver) {
            ((MultiLineReceiver) mEventsReceiver).done();
        }
    }

    public boolean clickOnView(String viewResourceId, String viewClass, int instanceIndex) {
        updateViews();

        for(View v: getViews()) {
            if(v.hasResourceId() && v.getViewResourceId().equalsIgnoreCase(viewResourceId) && v.getViewClass().equalsIgnoreCase(viewClass) && v.getInstanceIndex() == instanceIndex) {
                return clickOnView(v);
            }
        }

        return false;
    }

    public boolean clickOnView(String viewResourceId) {
        updateViews();

        for(View v: getViews()) {
            if(v.hasResourceId() && v.getViewResourceId().equalsIgnoreCase(viewResourceId)) {
                return clickOnView(v);
            }
        }

        return false;
    }

    public View findView(String resourceId) {
        updateViews();

        for(View v: getViews()) {
            if(v.hasResourceId() && v.getViewResourceId().equalsIgnoreCase(resourceId)) {
                return v;
            }
        }

        return null;
    }

    public boolean clickOnView(String itemId, String viewText) {
        updateViews();

        for(View v: getViews()) {
            if(v.hasResourceId() && v.getViewResourceId().equalsIgnoreCase(itemId) && v.getText()!=null && v.getText().equalsIgnoreCase(viewText)) {
                return clickOnView(v);
            }
        }

        return false;
    }

    public ArrayList<View> getViews(String storeFreeItemTextId) {
        updateViews();
        ArrayList<View> res = new ArrayList<View>(views.size());
        for(View v: getViews()) {
            if(v.hasResourceId() && v.getViewResourceId().equalsIgnoreCase(storeFreeItemTextId)) {
                res.add(v);
            }
        }

        return res;
    }

    public void setVerbosity(int level) {
        Constants.Configuration.VERBOSE_LEVEL = level;
    }

    public void swipeDown() {
        int w = getDisplayState().getDisplayWidth();
        int h = getDisplayState().getDisplayHeight();

        int x1 = w/2;
        int x2 = x1;
        int y1 = h-h/10;
        int y2 = h/4;

        swipe(x1,y1,x2,y2);
    }

    public void swipeUp() {
        int w = getDisplayState().getDisplayWidth();
        int h = getDisplayState().getDisplayHeight();

        int x1 = w/2;
        int x2 = x1;
        int y1 = h-h/10;
        int y2 = h/4;

        swipe(x1,y2,x2,y1);
    }
}
