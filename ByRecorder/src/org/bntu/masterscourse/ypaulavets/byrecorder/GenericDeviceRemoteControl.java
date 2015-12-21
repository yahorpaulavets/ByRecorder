package org.bntu.masterscourse.ypaulavets.byrecorder;

import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.core.IChimpDevice;
import com.android.ddmlib.*;
import org.bntu.masterscourse.ypaulavets.byrecorder.uicontrols.JButtonWithTag;
import org.bntu.masterscourse.ypaulavets.byrecorder.uicontrols.JCheckBoxWithTag;
import org.bntu.masterscourse.ypaulavets.byrecorder.uicontrols.JLabelWithDraw;
import org.bntu.masterscourse.ypaulavets.byrecorder.utils.GraphicsUtil;
import org.bntu.masterscourse.ypaulavets.byrecorder.utils.LoggerUtil;
import org.yahor.gobrotium.GoBro;
import org.yahor.gobrotium.config.ConfigurationManager;
import org.yahor.gobrotium.model.AndroidKeyEvent;
import org.yahor.gobrotium.model.DeviceDisplay;
import org.yahor.gobrotium.model.View;
import org.yahor.gobrotium.model.ViewBounds;
import org.yahor.gobrotium.utils.FileUtils;
import org.yahor.gobrotium.utils.L;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeMap;

public class GenericDeviceRemoteControl  extends JFrame implements ActionListener, MouseListener, MouseMotionListener, WindowListener {
    private static final int SAVE_EMULATOR_TEST_BUTTON_ID = 31;
    private static final int LOAD_EMULATOR_TEST_BUTTON_ID = 32;
    private static final int PWR_BUTTON_ID = 35;
    private static final int VUP_BUTTON_ID = 36;
    private static final int VDWN_BUTTON_ID = 37;
    private static final int BACK_BUTTON_ID = 38;
    private static final int HOME_BUTTON_ID = 39;
    private static final int ACCEPT_ROTATION_BUTTON_ID = 40;
    private static final int ROTATION_FACTORS_PANEL_HEIGHT = 220;
    private static final int SELECTED_VIEW_HEIGHT = 220;
    private static final int DPAD_DOWN_BUTTON_ID = 61;
    private static final int MENU_BUTTON_ID = 62;
    private static final int ENTER_BUTTON_ID = 63;
    private static final int DPAD_UP_BUTTON_ID = 64;
    private static final int DPAD_LEFT_BUTTON_ID = 65;
    private static final int DPAD_RIGHT_BUTTON_ID = 66;
    private static final int DPAD_CENTER_BUTTON_ID = 67;

    private static final int REMOTE_CONTROL_BUTTON_ID = 71;
    private static final int EDITOR_BUTTON_ID = 72;
    private static final int SELECT_DEVICE_BUTTON_ID = 73;
    private static GenericDeviceRemoteControl instance;

    private JMenuItem continueRecordingItem;
    private JMenuItem stopPlaying;
    private JMenuItem playTest;
    private JMenuItem stopRecordingItem;
    private JMenuItem startRecordingItem;

    protected JPanel deviceScreenPanel;
    protected JTextPane mTextPane;
    protected static final int SEND_TEXT_BUTTON_ID = 8;
    protected static final int LOAD_DEVICE_TEST_BUTTON_ID = 13;
    protected static final int GET_SCREENSHOT_AUTOMATICALLY = 24;
    protected static boolean isGroupControl = false;
    protected JButtonWithTag mBtnGetScreenshot;
    protected JLayeredPane mainPanel;
    protected JCheckBoxWithTag mChbxGetScreenshotAfterEachAction;
    protected int FRAME_WIDTH = 500;
    protected int FRAME_HEIGHT = 285;

    int MANAGE_TEST_W = 200;
    //
    int DEVICE_CONTROL_X = 10;
    int DEVICE_CONTROL_W = 180;


    private int MANAGE_DEVICE_ORIENTATION_Y = 5;
    private int MANAGE_DEVICE_ORIENTATION_X = DEVICE_CONTROL_X;

    int mDeviceLandscapeRotationFactor = 4;
    int mDevicePortraitRotationFactor = 3;
    int mDeviceReversePortraitRotationFactor = 2;
    int mDeviceReverseLandscapeRotationFactor = 1;


    private GoBro goBro;
    private Thread screenshotsThread;
    public static final String EVENT_X_MARKER = "0035";
    public static final String EVENT_Y_MARKER = "0036";
    private float scaleFactor = 3;
    private Thread recorderXYThread;

    private File screenshotsFolder;
    private float panelWidth;
    private float panelHeight;
    private Thread logcatThread;
    private JTextPane mLandscapeRotationFactorTextPane;
    private JTextPane mPortraitRotationFactorTextPane;
    private DeviceDisplay currentDisplayState;
    private String model;
    private String manufacturer;
    private String deviceName;
    private File devicePropsFile;
    private int lastXMarker = -1;
    private int lastYMarker = -1;
    private View lastMarkedView;
    private JLabelWithDraw scr;
    private JTextPane selectedViewText;
    private JTextPane selectedViewClass;
    private JTextPane selectedViewPackage;
    private JTextPane selectedViewResourceId;
    private JTextPane selectedViewContentDesc;
    private JTextPane selectedViewContentBounds;
    private int SELECTED_VIEW_Y = 5;
    private int SELECTED_VIEW_X = DEVICE_CONTROL_X + DEVICE_CONTROL_W + 20;
    private Thread monitoringThread;
    private JFrame devicePanelFrame;
    private JTextPane mReversePortraitRotationFactorTextPane;
    private JTextPane mReverseLandscapeRotationFactorTextPane;
    private Thread uiautomatorEventsThread;
    private Runnable uiautomatorEventsRunnable = new Runnable() {

        IShellOutputReceiver receiver = new IShellOutputReceiver() {
            @Override
            public void addOutput(byte[] bytes, int i, int i2) {
                try {
                    String str = new String(bytes, i, i2, "utf8");
                    L.i("STRE: " + str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void flush() {

            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };


        @Override
        public void run() {
            goBro.execShell("uiautomator events", receiver);
        }
    };

    private Thread testPlayerThread;
    private boolean isLogcatAllowed = false;

    public GenericDeviceRemoteControl(GoBro goBro) {
        setGoBro(goBro);

        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

        initControlView();

        startDeviceConnectionMonitoringThread();

        initDeviceProperties();
        readDeviceProperties();
        instance = this;
    }

    private IDevice[] currentDevicesList;
    Runnable deviceConnectionMonitor = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    IDevice[] devices = ConfigurationManager.getConfig().getDeviceBridge().getBridge().getDevices();
                    boolean oldDeviceIsStillPresent = false;
                    for(IDevice device: devices) {
                        if(device.getSerialNumber().equalsIgnoreCase(ConfigurationManager.getConfig().getDeviceSerialNumber())) {
                            oldDeviceIsStillPresent = true;
                            break;
                        }
                    }

                    for(IDevice device: devices) {
                        if (!oldDeviceIsStillPresent) {
                            L.e("!!! NEW DEVICE CONNECTED: " + device.getSerialNumber());
                            ConfigurationManager.getConfig().setDeviceSerialNumber(device.getSerialNumber());
                            ConfigurationManager.getConfig().getDeviceBridge().setTarget(device);
                            model = null;
                            manufacturer = null;

                            initDeviceProperties();
                            readDeviceProperties();

                            devicePanelFrame.setTitle(ConfigurationManager.getConfig().getDeviceSerialNumber());
                        }
                    }

                    currentDevicesList = devices;

                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static GenericDeviceRemoteControl getInstance() {
        return instance;
    }

    private void startDeviceConnectionMonitoringThread() {
        monitoringThread = new Thread(deviceConnectionMonitor);
        monitoringThread.start();
    }

    private void startUiAutomatorEventsMonitoringThread() {
        uiautomatorEventsThread = new Thread(uiautomatorEventsRunnable);
        uiautomatorEventsThread.start();
    }

    private void readDeviceProperties() {
        if(devicePropsFile.exists()) {
            Properties properties = new Properties();

            try {
                properties.load(new FileInputStream(devicePropsFile));
                String l = properties.getProperty("landscape");
                String p = properties.getProperty("portrait");
                String rp = properties.getProperty("reverse_portrait");
                String rl = properties.getProperty("reverse_landscape");

                int l_int = 4;
                int p_int = 3;
                int rp_int = 2;
                int rl_int = 1;
                try {
                    l_int = Integer.parseInt(l);
                    p_int = Integer.parseInt(p);
                    rp_int = Integer.parseInt(rp);
                    rl_int = Integer.parseInt(rl);
                } catch (NumberFormatException ignored) {
                    ignored.printStackTrace();
                }

                mDeviceLandscapeRotationFactor = l_int;
                mDevicePortraitRotationFactor = p_int;

                mDeviceReverseLandscapeRotationFactor = rl_int;
                mDeviceReversePortraitRotationFactor = rp_int;

                mLandscapeRotationFactorTextPane.setText(l_int + "");
                mPortraitRotationFactorTextPane.setText(p_int+"");
                mReversePortraitRotationFactorTextPane.setText(rp_int+"");
                mReverseLandscapeRotationFactorTextPane.setText(rl_int + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initDeviceProperties() {
        // TODO save device props
        MultiLineReceiver modelMultiLineReceiver = new MultiLineReceiver() {
            @Override
            public void processNewLines(String[] strings) {
                if(model != null && model.length() > 0) {
                    return;
                }
                model = strings[0];
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };

        MultiLineReceiver manufMultiLineReceiver = new MultiLineReceiver() {
            @Override
            public void processNewLines(String[] strings) {
                if(manufacturer != null && manufacturer.length() > 0) {
                    return;
                }
                manufacturer = strings[0];
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };

        goBro.execShell("getprop ro.product.manufacturer", manufMultiLineReceiver);
        goBro.execShell("getprop ro.product.model", modelMultiLineReceiver);
        deviceName = manufacturer+"-"+model;
        deviceName = deviceName.trim().replaceAll("\\s", "-");
        this.setTitle(deviceName + " SN: " + ConfigurationManager.getConfig().getDeviceSerialNumber());

        L.e(manufacturer + "-" + model);
        devicePropsFile = new File(deviceName+".properties");
    }

    public void initControlView() {
        initComponents();
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addWindowListener(this);
        startScreenshotsCapturingThread();
    }

    private void startScreenshotsCapturingThread() {

        if(screenshotsThread != null) {
            while (screenshotsThread.isAlive()) {
                screenshotsThread.interrupt();
            }
        }

        screenshotsThread = new Thread(screenshotsRunnable);
        screenshotsThread.start();
    }

    private boolean isScreenshotsAllowed = true;
    private Runnable screenshotsRunnable = new Runnable() {
        @Override
        public void run() {
            while(isScreenshotsAllowed) {
                try {
                    long start = System.currentTimeMillis();
                    refreshDeviceState();
                    L.i(String.format("H: %d, W: %d, Is landscape? > %s", currentDisplayState.getDisplayHeight(), currentDisplayState.getDisplayWidth(), currentDisplayState.isLandscape()+""));

                    processFB0Screenshot();

                    if(start < 600) {
                        Thread.sleep(600-start);
                    }

                    L.e("SCREENSHOT CAPTURE TIME: " + (System.currentTimeMillis()-start));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    protected void initComponents() {
        mainPanel = new JLayeredPane();

        mainPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        mainPanel.setBorder(new LineBorder(Color.black, 0));
        mainPanel.setBackground(Color.WHITE);
        this.setBackground(Color.WHITE);
        JPanel panelBlue = new JPanel();
        this.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        panelBlue.setBackground(Color.GRAY);
        panelBlue.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        panelBlue.setOpaque(true);

        initDeviceScreenPanel();

        Insets insets = new Insets(0,0,0,0);
        insets.left = 0;
        insets.right = 0;

        mainPanel.add(panelBlue, 0, 0);

        mainPanel.add(initRotationPanel(), 1, 0);
        mainPanel.add(createSelectedViewInfoPanel(), 1, 0);
        mainPanel.add(createFooterPanel(), 1, 0);

        this.setTitle("Remote control.");//: " + mDevice.toString());
        this.pack();
        this.validate();
        this.getRootPane().updateUI();
    }

    private Component createFooterPanel() {
        JPanel footerPanel = new JPanel(new GridLayout(1, 3));
        footerPanel.setBorder(new TitledBorder("Mission control"));
        footerPanel.setBounds(10, SELECTED_VIEW_HEIGHT + 10, FRAME_WIDTH-10, 50);
        footerPanel.setOpaque(true);
        footerPanel.setBackground(Color.white);

        footerPanel.add(new JButtonWithTag("Remote control", REMOTE_CONTROL_BUTTON_ID, this));
        footerPanel.add(new JButtonWithTag("Test case editor", EDITOR_BUTTON_ID, this));
        footerPanel.add(new JButtonWithTag("Select device", SELECT_DEVICE_BUTTON_ID, this));

        return footerPanel;
    }

    private Component createSelectedViewInfoPanel() {
        JPanel selectedViewPanel = new JPanel(new GridLayout(12, 1));
        selectedViewPanel.setBorder(new TitledBorder("Selected view info"));
        selectedViewPanel.setBounds(SELECTED_VIEW_X + 30, SELECTED_VIEW_Y, MANAGE_TEST_W+60, SELECTED_VIEW_HEIGHT);
        selectedViewPanel.setOpaque(true);
        selectedViewPanel.setBackground(Color.white);

        Label lblText = new Label("View text:");
        Label lblPackage = new Label("View package");
        Label lblClass = new Label("View class");
        Label lblContentDesc = new Label("View content-desc");
        Label lblResourceId = new Label("View resource id");
        Label lblBounds = new Label("View bounds");

        selectedViewText = new JTextPane();
        selectedViewClass = new JTextPane();
        selectedViewPackage = new JTextPane();
        selectedViewContentDesc = new JTextPane();
        selectedViewContentBounds = new JTextPane();
        selectedViewResourceId = new JTextPane();
        selectedViewText.setBorder(new LineBorder(Color.black, 1));
        selectedViewClass.setBorder(new LineBorder(Color.black, 1));
        selectedViewPackage.setBorder(new LineBorder(Color.black, 1));
        selectedViewContentDesc.setBorder(new LineBorder(Color.black, 1));
        selectedViewContentBounds.setBorder(new LineBorder(Color.black, 1));
        selectedViewResourceId.setBorder(new LineBorder(Color.black, 1));

        selectedViewPanel.add(lblText);
        selectedViewPanel.add(selectedViewText);
        selectedViewPanel.add(lblPackage);
        selectedViewPanel.add(selectedViewPackage);
        selectedViewPanel.add(lblClass);
        selectedViewPanel.add(selectedViewClass);

        selectedViewPanel.add(lblContentDesc);
        selectedViewPanel.add(selectedViewContentDesc);

        selectedViewPanel.add(lblResourceId);
        selectedViewPanel.add(selectedViewResourceId);

        selectedViewPanel.add(lblBounds);
        selectedViewPanel.add(selectedViewContentBounds);

        return selectedViewPanel;
    }

    private Component initRotationPanel() {
        JPanel panelRotations = new JPanel(new GridLayout(9, 1));
        panelRotations.setBorder(new TitledBorder("Manage device orientation"));
        panelRotations.setBounds(MANAGE_DEVICE_ORIENTATION_X, MANAGE_DEVICE_ORIENTATION_Y, MANAGE_TEST_W, ROTATION_FACTORS_PANEL_HEIGHT);
        panelRotations.setOpaque(true);
        panelRotations.setBackground(Color.white);

        Label landscapeFactorLabel = new Label("Landscape rotation factor");
        Label portraitFactorLabel = new Label("Portrait rotation factor");
        Label reversePortraitFactorLabel = new Label("Reverse Portrait rotation factor");
        Label reverseLandscapeFactorLabel = new Label("Reverse Portrait rotation factor");

        mLandscapeRotationFactorTextPane = new JTextPane();
        mLandscapeRotationFactorTextPane.setBorder(new LineBorder(Color.black, 1));

        mPortraitRotationFactorTextPane = new JTextPane();
        mPortraitRotationFactorTextPane.setBorder(new LineBorder(Color.black, 1));

        mReversePortraitRotationFactorTextPane = new JTextPane();
        mReversePortraitRotationFactorTextPane.setBorder(new LineBorder(Color.black, 1));

        mReverseLandscapeRotationFactorTextPane = new JTextPane();
        mReverseLandscapeRotationFactorTextPane.setBorder(new LineBorder(Color.black, 1));

        mLandscapeRotationFactorTextPane.setText(mDeviceLandscapeRotationFactor + "");
        mPortraitRotationFactorTextPane.setText(mDevicePortraitRotationFactor+"");
        mReversePortraitRotationFactorTextPane.setText(mDeviceReversePortraitRotationFactor+"");
        mReverseLandscapeRotationFactorTextPane.setText(mDeviceReverseLandscapeRotationFactor +"");

        panelRotations.add(landscapeFactorLabel);
        panelRotations.add(mLandscapeRotationFactorTextPane);
        panelRotations.add(portraitFactorLabel);
        panelRotations.add(mPortraitRotationFactorTextPane);
        panelRotations.add(reversePortraitFactorLabel);
        panelRotations.add(mReversePortraitRotationFactorTextPane);
        panelRotations.add(reverseLandscapeFactorLabel);
        panelRotations.add(mReverseLandscapeRotationFactorTextPane);

        JButtonWithTag mBtnSetNewRotationFactors = new JButtonWithTag("Set factors", ACCEPT_ROTATION_BUTTON_ID, this);
        panelRotations.add(mBtnSetNewRotationFactors);

        return panelRotations;
    }

    private void initDeviceScreenPanel() {
        deviceScreenPanel = new JPanel(new BorderLayout());
        refreshDeviceState();

        deviceScreenPanel.setOpaque(true);
        deviceScreenPanel.setBackground(Color.gray);
        deviceScreenPanel.addMouseListener(this);
        deviceScreenPanel.addMouseMotionListener(this);

        setDevicePanelBounds();

        devicePanelFrame = new JFrame();
        devicePanelFrame.getContentPane().setPreferredSize(new Dimension(deviceScreenPanel.getWidth(), deviceScreenPanel.getHeight() + 110));
        devicePanelFrame.getContentPane().setLayout(new BoxLayout(devicePanelFrame.getContentPane(), BoxLayout.Y_AXIS));
        devicePanelFrame.getContentPane().add(deviceScreenPanel, BorderLayout.PAGE_START);
        devicePanelFrame.setTitle(ConfigurationManager.getConfig().getDeviceSerialNumber());

        JButtonWithTag mBtnPower = new JButtonWithTag("\u220E", PWR_BUTTON_ID, this);
        JButtonWithTag mBtnVUP = new JButtonWithTag("V+", VUP_BUTTON_ID, this);
        JButtonWithTag mBtnVDOWN = new JButtonWithTag("v-", VDWN_BUTTON_ID, this);
        JButtonWithTag mBtnBACK = new JButtonWithTag("\u21a9", BACK_BUTTON_ID, this);
        JButtonWithTag mBtnHOME = new JButtonWithTag("\u2302", HOME_BUTTON_ID, this);
        JButtonWithTag mBtnMenu= new JButtonWithTag("\u2261", MENU_BUTTON_ID, this);
        JButtonWithTag mBtnEnter = new JButtonWithTag("\u21b2", ENTER_BUTTON_ID, this);
        JButtonWithTag mBtnUp = new JButtonWithTag("\u25b2", DPAD_UP_BUTTON_ID, this);
        mBtnUp.setBackground(Color.BLUE);
        JButtonWithTag mBtnDown = new JButtonWithTag("\u25bc", DPAD_DOWN_BUTTON_ID, this);
        mBtnDown.setBackground(Color.BLUE);
        JButtonWithTag mBtnLeft = new JButtonWithTag("\u25c0", DPAD_LEFT_BUTTON_ID, this);
        mBtnLeft.setBackground(Color.BLUE);
        JButtonWithTag mBtnRight = new JButtonWithTag("\u25b6", DPAD_RIGHT_BUTTON_ID, this);
        mBtnRight.setBackground(Color.BLUE);
        JButtonWithTag mBtnCenter = new JButtonWithTag("OK", DPAD_CENTER_BUTTON_ID, this);
        mBtnCenter.setBackground(Color.GREEN);

        mTextPane = new JTextPane();
        mTextPane.setPreferredSize(new Dimension(100, 20));

        JPanel firstRowPanel = new JPanel();
        firstRowPanel.setLayout(new GridLayout(5,3));

        firstRowPanel.add(mBtnPower);
        firstRowPanel.add(mBtnUp);
        firstRowPanel.add(mBtnHOME);

        firstRowPanel.add(mBtnLeft);
        firstRowPanel.add(mBtnCenter);
        firstRowPanel.add(mBtnRight);

        firstRowPanel.add(mBtnBACK);
        firstRowPanel.add(mBtnDown);
        firstRowPanel.add(mBtnMenu);

        firstRowPanel.add(mBtnVUP);
        firstRowPanel.add(mBtnVDOWN);
        firstRowPanel.add(mBtnEnter);

        firstRowPanel.add(mTextPane);
        firstRowPanel.add(new JButtonWithTag("Enter text", SEND_TEXT_BUTTON_ID, this));

        devicePanelFrame.getContentPane().add(firstRowPanel);
        devicePanelFrame.setResizable(false);
        devicePanelFrame.setAlwaysOnTop(true);
        devicePanelFrame.setJMenuBar(new EmulatorMenuBar());

        devicePanelFrame.pack();

        devicePanelFrame.setVisible(true);
    }

    private void refreshDeviceState() {
        currentDisplayState = goBro.getDisplayState();
    }

    private void setDevicePanelBounds() {
        int width = currentDisplayState.getDisplayWidth();
        int height = currentDisplayState.getDisplayHeight();

        if (height < 1300) {
            scaleFactor = 2;
        } else if (height < 2000) {
            scaleFactor = 3;
        } else {
            L.e("Unmapped screen height :: ");
            scaleFactor = 4;
        }

        panelWidth = width / scaleFactor;
        panelHeight = height / scaleFactor;

        deviceScreenPanel.setBackground(Color.BLUE);
        deviceScreenPanel.setSize((int)panelWidth, (int)panelHeight);

        if(devicePanelFrame == null) {
            return;
        }

        devicePanelFrame.setBackground(Color.WHITE);
        devicePanelFrame.getContentPane().setPreferredSize(new Dimension(deviceScreenPanel.getWidth(),deviceScreenPanel.getHeight()+110));
        devicePanelFrame.pack();
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (!(source instanceof JButtonWithTag)) {
            if (source instanceof JCheckBoxWithTag) {
                if (((JCheckBoxWithTag) source).getId() == GET_SCREENSHOT_AUTOMATICALLY) {
                    applyDoScreenshotsAutomatically();
                }
            }
            return;
        }

        JButtonWithTag btn = (JButtonWithTag) source;

        switch (btn.getId()) {
            case EDITOR_BUTTON_ID:
                handleEditorClick();
                break;

            case SELECT_DEVICE_BUTTON_ID:
                handleDeviceSelection();
                break;

            case REMOTE_CONTROL_BUTTON_ID:
                handleClickOnRemoteControlButton();
                break;

            case SEND_TEXT_BUTTON_ID:
                sendText();
                break;

            case LOAD_DEVICE_TEST_BUTTON_ID:
                if(RecordingManager.getInstance().isTestPlaying()) { return; }
                loadTest(HistoryType.DEVICE);
                break;

            case SAVE_EMULATOR_TEST_BUTTON_ID:
                saveRecordedTest(HistoryType.EMULATOR);
                break;

            case LOAD_EMULATOR_TEST_BUTTON_ID:
                loadTest(HistoryType.EMULATOR);
                break;

            case PWR_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_POWER);
                break;

            case VUP_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_VOLUME_UP);
                break;

            case VDWN_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_VOLUME_DOWN);
                break;

            case HOME_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_HOME);
                break;

            case MENU_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_MENU);
                break;

            case DPAD_DOWN_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_DPAD_DOWN);
                break;

            case DPAD_UP_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_DPAD_UP);
                break;

            case DPAD_LEFT_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_DPAD_LEFT);
                break;

            case DPAD_RIGHT_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_DPAD_RIGHT);
                break;

            case DPAD_CENTER_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_DPAD_CENTER);
                break;

            case ENTER_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_ENTER);
                break;

            case BACK_BUTTON_ID:
                processButton(AndroidKeyEvent.KEY_CODE_BACK);
                break;

            case ACCEPT_ROTATION_BUTTON_ID:
                acceptNewRotationFactors();
                break;
        }

        System.out.println("Btn id clicked: " + btn.getId());
    }

    private void handleEditorClick() {
        JTestRecordingFrame.getInstance().setVisible(true);
        JTestRecordingFrame.getInstance().loadTestCase(RecordingManager.getInstance());
    }

    private void handleDeviceSelection() {
        if(currentDevicesList != null && currentDevicesList.length > 1) {
            Object[] possibilities = new Object[currentDevicesList.length];
            for(int index = 0; index < possibilities.length; index++) {
                possibilities[index] = currentDevicesList[index].getSerialNumber();
            }

            String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select device for remote\ncontrol by serial number",
                    "Select device",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    possibilities,
                    "none");

            if ((s != null) && (s.length() > 0)) {
                IDevice targetDevice = null;
                for(IDevice device: currentDevicesList) {
                    if(device.getSerialNumber().equalsIgnoreCase(s)) {
                        targetDevice = device;
                        break;
                    }
                }

                if(targetDevice == null) {
                    return;
                }

                L.e("!!! NEW DEVICE CONNECTED: " + targetDevice.getSerialNumber());
                ConfigurationManager.getConfig().setDeviceSerialNumber(targetDevice.getSerialNumber());
                ConfigurationManager.getConfig().getDeviceBridge().setTarget(targetDevice);
                model = null;
                manufacturer = null;

                initDeviceProperties();
                readDeviceProperties();

                devicePanelFrame.setTitle(ConfigurationManager.getConfig().getDeviceSerialNumber());
            }
        }
    }

    private void handleClickOnRemoteControlButton() {
        if(devicePanelFrame == null) {
            initDeviceScreenPanel();
        } else if(!devicePanelFrame.isShowing()) {
            devicePanelFrame.setVisible(true);
        } else {
            devicePanelFrame.setVisible(false);
        }
    }

    private void sendText() {
        if(RecordingManager.getInstance().isTestPlaying()) {
            recordText(mTextPane.getText());
        }

        if(RecordingManager.getInstance().isTestPlaying()) { return; }

        goBro.inputText(mTextPane.getText());
    }

    private void processButton(int keyCode) {
        if(RecordingManager.getInstance().isTestRecording()) {
            recordKeyEvent(new ShellKeyEventHistoryItem(keyCode));
        }
        if(RecordingManager.getInstance().isTestPlaying()) { return; }
        goBro.inputKeyEvent(keyCode);
    }

    private void acceptNewRotationFactors() {
        String portrait = mPortraitRotationFactorTextPane.getText();
        String landscape = mLandscapeRotationFactorTextPane.getText();

        String rPortrait = mReversePortraitRotationFactorTextPane.getText();
        String rLandscape = mReverseLandscapeRotationFactorTextPane.getText();

        try {
            int p = Integer.parseInt(portrait);
            int l = Integer.parseInt(landscape);

            if(p < 0 || l < 0 || p > 4 || l > 4) {
                throw new NumberFormatException("Value should not exceed 4, because only 4 rotations are possible");
            }

            mDeviceLandscapeRotationFactor = l;
            mDevicePortraitRotationFactor = p;

            int rp = Integer.parseInt(rPortrait);
            int rl = Integer.parseInt(rLandscape);

            if(rp < 0 || rl < 0 || rp > 4 || rl > 4) {
                throw new NumberFormatException("Value should not exceed 4, because only 4 rotations are possible");
            }

            mDeviceReversePortraitRotationFactor = rp;
            mDeviceReverseLandscapeRotationFactor = rl;


            saveDeviceConfig(l,p,rp,rl);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            mPortraitRotationFactorTextPane.setText(mDevicePortraitRotationFactor+"");
            mLandscapeRotationFactorTextPane.setText(mDeviceLandscapeRotationFactor+"");
            mPortraitRotationFactorTextPane.setText(mDeviceReversePortraitRotationFactor+"");
            mLandscapeRotationFactorTextPane.setText(mDeviceReverseLandscapeRotationFactor+"");
        }
    }

    private void saveDeviceConfig(int landscape, int portrait, int reverse_portrait, int reverse_landscape) {
        Properties properties = new Properties();
        properties.setProperty("landscape", String.valueOf(landscape));
        properties.setProperty("portrait", String.valueOf(portrait));
        properties.setProperty("reverse_portrait", String.valueOf(reverse_portrait));
        properties.setProperty("reverse_landscape", String.valueOf(reverse_landscape));

        try {
            properties.store(new FileOutputStream(devicePropsFile), "Device properties for: " + deviceName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordKeyEvent(ShellKeyEventHistoryItem event) {
        RecordingManager.getInstance().record(event);
//        RecordingManager.getInstance().putInHistory(event);
    }

    private void startNewFolder() {
        screenshotsFolder = new File("./test_run_"+System.currentTimeMillis());
        if(!screenshotsFolder.exists()) screenshotsFolder.mkdir();
    }

    private void playEmulatorEventsHistoryXY(long testCaseId) {
        // todo fix test playing after it was removed
        ArrayList<TestCaseItem> history = RecordingManager.getInstance().getTestCase(testCaseId).getTestCaseSteps();
        RecordingManager.getInstance().getTestCase(testCaseId).getId();

        for (TestCaseItem testCaseItem : history) {
            testCaseItem.setPlayed(false);
        }

        JTestRecordingFrame.getInstance().updateUI();
        JTestRecordingFrame.getInstance().startNewReport();

        for (TestCaseItem testCaseItem : history) {
            if(testCaseItem instanceof PauseHistoryItem) {
                try {
                    Thread.sleep(((PauseHistoryItem) testCaseItem).getPause());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    L.e("Test player is aborted.");
                    return;
                }
                testCaseItem.setResult(TestStepResult.BLIND_STEP);
                JTestRecordingFrame.getInstance().onHistoryItemPlayed(testCaseId, testCaseItem);
            } else if(testCaseItem instanceof ShellKeyEventHistoryItem) {
                playKeyEvent(((ShellKeyEventHistoryItem) testCaseItem).getKeyCode());
                testCaseItem.setResult(TestStepResult.BLIND_STEP);
                JTestRecordingFrame.getInstance().onHistoryItemPlayed(testCaseId, testCaseItem);
            } else if(testCaseItem instanceof TextHistoryItem) {
                playInputText(((TextHistoryItem) testCaseItem).getInputText());
                testCaseItem.setResult(TestStepResult.BLIND_STEP);
                JTestRecordingFrame.getInstance().onHistoryItemPlayed(testCaseId, testCaseItem);
            } else if (testCaseItem.getTag() != null) {
                playSwipe((TouchTestCaseItem)testCaseItem);
                testCaseItem.setResult(TestStepResult.BLIND_STEP);
                JTestRecordingFrame.getInstance().onHistoryItemPlayed(testCaseId, testCaseItem);
            } else {
                TestStepResult result = playTouch(testCaseItem);
                testCaseItem.setResult(result);
                JTestRecordingFrame.getInstance().onHistoryItemPlayed(testCaseId, testCaseItem);
                if(result == TestStepResult.FAIL) {
                    // TODO: test case failed.. what next?
                    testCaseItem.setPlayed(true);
                    JTestRecordingFrame.getInstance().updateUI();
                    break;
                }
            }

            testCaseItem.setPlayed(true);
            JTestRecordingFrame.getInstance().updateUI();
        }
    }

    private void playKeyEvent(int keyCode) {
        goBro.inputKeyEvent(keyCode);
    }

    private void playInputText(String inputText) {
        goBro.inputText(inputText);
    }

    private TestStepResult playTouch(TestCaseItem p) {
        View v = p.getView();

        if(v!=null && v.hasResourceId()) {
            L.i("1 TRYING TO CLICK BY RESOURCE ID: " + v.getViewResourceId() + " " + v.getIndex() +" " + v.getInstanceIndex());
            boolean res = goBro.clickOnView(v.getViewResourceId(), v.getViewClass(), v.getInstanceIndex());
            return res ? TestStepResult.OK : TestStepResult.FAIL;
        }


        if(v != null &&
                (v.getViewClass().contains("Text") ||
                        v.getViewClass().contains("Check") ||
                        v.getViewClass().contains("Button"))) {
            String text = v.getText();
            boolean isContentDescription = false;
            if(text == null || text.length() == 0) {
                text = v.getContentDescription();
                isContentDescription = true;
            }

            if(text != null && text.length() > 0) { // NOTE: trying to click on text or content description of the view
                L.i("TRYING TO CLICK ON TEXT: " + text);
                boolean res;
                if(isContentDescription) {
                    res = goBro.clickOnContentDescription(text);
                } else {
                    res = goBro.clickOnText(text);
                }
                return res ? TestStepResult.OK : TestStepResult.FAIL;
            }
        } else  if (v != null) {     // NOTE: trying to click on index based approach (view class + its index on the screen
            ArrayList<View> views = goBro.getCurrentViews(v.getViewClass());
            if(views.size() -1 >= v.getIndex()) {
                L.e("Trying to click on: " + v.getViewClass() + " / " + v.getIndex() + " / views: " + views.size());
                goBro.clickOnView(views.get(v.getIndex()));
                return TestStepResult.BLIND_STEP;
            }
        }

        performClickOnDevice(p.getX(), p.getY());
        return TestStepResult.BLIND_STEP;
    }

    private void playSwipe(TouchTestCaseItem p) {
        TouchTestCaseItem end = (TouchTestCaseItem) p.getTag();

        goBro.swipe(p.getX(), p.getY(), end.getX(), end.getY());
    }

    private void startRecording_Emulator(boolean clearCurrentRecording) {
        if(RecordingManager.getInstance().isTestRecording()) {
            L.e("Already recording");
            return;
        }

        RecordingManager.getInstance().setTestRecording(true);

        startRecordingItem.setEnabled(false);
        continueRecordingItem.setEnabled(false);
        stopRecordingItem.setEnabled(true);
        playTest.setEnabled(false);
        stopPlaying.setEnabled(false);

        if(clearCurrentRecording) {
            RecordingManager.getInstance().startNewTestCase();
            JTestRecordingFrame.getInstance().addTestCaseNode(RecordingManager.getInstance().getCurrentTestCase().getId());
        }
    }

    private void recordText(String text) {
        RecordingManager.getInstance().record(text);
    }

    public void mouseClicked(MouseEvent e) {
        // ignore
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // ignore
    }

    ArrayList<MouseEvent> swipeEvents = new ArrayList<MouseEvent>(1000);
    public void mouseReleased(MouseEvent e) {

        if(RecordingManager.getInstance().isTestPlaying()) {
            return;
        }

        resetMarkers();
        if(e.getButton() == MouseEvent.BUTTON3) {
            setSelectedViewMarkers((int) (e.getX() * scaleFactor), (int) (e.getY() * scaleFactor));
            analyseViews(true);

            test();

            return;
        }

        if(swipeEvents.size() > 1) {

            if(RecordingManager.getInstance().isTestRecording()) {
                recordSwipe(swipeEvents);
            }
            doSwipe();
            swipeEvents.clear();
        } else {
            int x =  (int) (e.getX() * scaleFactor);
            int y =  (int ) (e.getY() * scaleFactor);

            if(RecordingManager.getInstance().isTestRecording()) {
                View v = detectClickedView(x, y);

                if(v!= null && !v.getViewClass().contains("TextView")) {
                    ArrayList<View> views = goBro.getCurrentViews(v.getViewClass());
                    for (int i = 0; i < views.size(); i++) {
                        View view = views.get(i);
                        if (v.getBounds().toString().equalsIgnoreCase(view.getBounds().toString())) {
                            L.e("NON TEXT VIEW WAS CLICKED, INDEX: " + i + " VIEW: " + v);
                            v.setIndex(i);
                            L.e("NON TEXT VIEW WAS CLICKED, INDEX: " + i + " VIEW: " + v);
                        }
                    }
                }

                recordTouch(x, y, v);
            }

            performClickOnDevice(x, y);
        }
    }

    boolean started = false;

    private void test() {
        if(started) { return; }
        started = true;



        ArrayList<View> children = goBro.getCurrentViews("android.widget.ListView", true).get(0).children;

//        goBro.execShell("uiautomator events", new MultiLineReceiver() {
//            @Override
//            public void processNewLines(String[] strings) {
//
//                for(String str: strings) {
//                    L.d("LINES: " + str);
//                    if(str.contains("ListView")) {
//                        L.d("LISTVIEW ACTIVITY: " + str);
//                    }
//                }
//            }
//
//            @Override
//            public boolean isCancelled() {
//                return false;
//            }
//        });

        for(View v: children) {
            if(!v.getViewClass().contains("LinearLayout")) { continue; }
            View childV = v.findChild("android:id/title");
            if(childV == null) { continue; }
            goBro.clickOnView(childV);
            goBro.sleep(1);
            if(goBro.getDisplayState().getCurrentlyFocusedApp().contains("Sub")) {
                goBro.pressBackButton();
                goBro.sleep(1);
            }
        }
    }

    private void setSelectedViewInfo(View v) {
        String text = v.getText();
        String viewPackage = v.getViewPackage();
        String viewClass = v.getViewClass();
        String viewContentDesc = v.getContentDescription();
        String viewBounds = v.getBounds().toString();
        String resourceId = v.getViewResourceId();

        L.e("RESOURCE ID: " + resourceId + " position: " + v.getViewLayerPosition());

//        printDebugParent(v);
//        printDebugSiblings(v);

        selectedViewText.setText(text == null ? "" : text);
        selectedViewPackage.setText(viewPackage);
        selectedViewClass.setText(viewClass);
        selectedViewContentDesc.setText(viewContentDesc == null ? "" : viewContentDesc);
        selectedViewResourceId.setText(resourceId == null ? "" : resourceId);
        selectedViewContentBounds.setText(viewBounds);
    }

    private void printDebugSiblings(View v) {
        View parent = v.getParentView();
        if(!parent.hasChildren()) {
            return;
        }

        L.d("ME: " + v.getViewLayerPosition() + " " + v);
        for(View child: parent.children) {
            L.d("MY SIBLING: " + child.getViewLayerPosition() + " " + child);
        }
    }

    private void printDebugParent(View v) {
        try {
            View parent = v.getParentView();
            while(parent != null) {
                L.d(parent.getViewLayerPosition() + " PARENT VIEW: " + parent);

                if(parent.hasChildren()) {
                    for(View child: parent.children) {
                        L.d("\t\t\t"+child.getViewLayerPosition()+ " \t" +child);
                    }
                }

                parent = parent.getParentView();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setSelectedViewMarkers(int x, int y) {
        setViewMarker(x, y);
    }

    private void setViewMarker(int x, int y) {
        setLastXMarker(x);
        setLastYMarker(y);
    }

    private void performClickOnDevice(int x, int y) {
        L.i("Trying to do a click on the device: " + x + " " + y);
        goBro.clickOnXY(x, y);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignore
    }

    private void recordTouch(int x, int y, View v) {
//        RecordingManager.getInstance().putInHistory(new TouchTestCaseItem(goBro.getLastRotationIndex(), v, x, y, System.currentTimeMillis()));

        L.d("RECORDING TOUCH:        " +v + " /// " + v.getViewLayerPosition());

        RecordingManager.getInstance().record(new TouchTestCaseItem(goBro.getLastRotationIndex(), v, x, y, System.currentTimeMillis()));
    }

    private void recordSwipe(ArrayList<MouseEvent> swipeEvents) {
        MouseEvent start = swipeEvents.get(0);
        MouseEvent finish = swipeEvents.get(swipeEvents.size() - 1);
        TouchTestCaseItem ptStart = new TouchTestCaseItem(start.getX() * (int)scaleFactor, start.getY() * (int)scaleFactor, System.currentTimeMillis());
        TouchTestCaseItem ptFinish = new TouchTestCaseItem(finish.getX() * (int)scaleFactor, finish.getY() * (int)scaleFactor, System.currentTimeMillis());
        ptStart.setTag(ptFinish);
        ptStart.setIsSwipe(true);

//        RecordingManager.getInstance().putInHistory(ptStart);
        RecordingManager.getInstance().record(ptStart);
    }

    private void doSwipe() {
        MouseEvent eventStart = swipeEvents.get(0);
        MouseEvent eventFinish = swipeEvents.get(swipeEvents.size()-1);

        int startX = eventStart.getX() * (int)scaleFactor;
        int startY = eventStart.getY() * (int)scaleFactor;

        int endX = eventFinish.getX() * (int)scaleFactor;
        int endY = eventFinish.getY() * (int)scaleFactor;

        goBro.swipe(startX, startY, endX, endY);
    }

    public void mouseDragged(MouseEvent e) {
        Object source = e.getSource();
        if (source instanceof JLayeredPane) {
            return;
        }
        // TODO: think about swipe
        swipeEvents.add(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // ignore
    }

    protected void processFB0Screenshot() {
        processFB0Screenshot(true);
    }

    protected void processFB0Screenshot(boolean shouldUpdateUIWithImage) {
        if(!isScreenshotsAllowed) {
            return;
        }

        if (RecordingManager.getInstance().isTestPlaying()) {
            LoggerUtil.log("Recording screenshots sequence.");
        }

        if(shouldUpdateUIWithImage) {
            L.e("ROTATION: " + goBro.getLastRotationIndex());
            analyseViews(true);
            updateUiScr();
            if(lastMarkedView != null) {
                if(scr != null){
                    scr.setNewBounds(lastMarkedView.getBounds(), scaleFactor);
                    deviceScreenPanel.updateUI();
                }
            }
        }
    }

    private View detectClickedView(int x, int y) {
        View previousBest = null;
        Long start = System.currentTimeMillis();
        for(View v: goBro.getViews()) {
            if(isViewMarked(v, previousBest, x, y)) {
                previousBest = v;
            }
        }

        L.e("TIME TO LOAD XML HIERARCHY: " + (System.currentTimeMillis()-start));

        return previousBest;
    }

    private void analyseViews(boolean drawClickedViewBounds) {
        if(getLastYMarker() < 0 || getLastXMarker() < 0) return;
        lastMarkedView = detectClickedView(getLastXMarker(), getLastYMarker());

        L.e("Marked view: " + lastMarkedView);

        if(!drawClickedViewBounds) return;

        if(lastMarkedView != null) {
            setSelectedViewInfo(lastMarkedView);
            if(scr != null){
                scr.setNewBounds(lastMarkedView.getBounds(), scaleFactor);
                deviceScreenPanel.updateUI();
            }
        }
    }

    private boolean isViewMarked(View currentView, View previousBest,int x, int y) {
        ViewBounds viewBounds = currentView.getBounds();

        boolean isHit = (viewBounds.getUpperLeftX() <= x && viewBounds.getBottomRightX() >= x &&
                viewBounds.getUpperLeftY() <= y && viewBounds.getBottomRightY() >= y);

        if(isHit && previousBest != null) {

            int previousViewHeight =  previousBest.getBounds().getBottomRightY() - previousBest.getBounds().getUpperLeftY();
            int previousViewWidth =  previousBest.getBounds().getBottomRightX() - previousBest.getBounds().getUpperLeftX();

            int currentViewHeight =  currentView.getBounds().getBottomRightY() - currentView.getBounds().getUpperLeftY();
            int currentViewWidth =  currentView.getBounds().getBottomRightX() - currentView.getBounds().getUpperLeftX();

            return currentViewHeight <= previousViewHeight && currentViewWidth <= previousViewWidth;
        }

        return isHit;
    }

    private void resetMarkers() {
        setLastXMarker(-1);
        setLastYMarker(-1);
        lastMarkedView = null;
    }

    private void updateUiScr() {
        try {
            scr = loadScreenShot((int)panelWidth, (int)panelHeight);

            if (deviceScreenPanel.getComponentCount() > 0) {
                deviceScreenPanel.remove(0);
            }
            scr.setLocation(0,0);
            deviceScreenPanel.add(scr, 0);
            deviceScreenPanel.updateUI();
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public JLabelWithDraw loadScreenShot(int w, int h) throws IOException, TimeoutException, AdbCommandRejectedException {
        BufferedImage myPicture;
        try {
            String fileName = "./test.png";
            if(RecordingManager.getInstance().isTestPlaying()) {
                fileName= new File(
                        screenshotsFolder.getAbsolutePath(),
                        "test_"+System.currentTimeMillis()+".png")
                        .getAbsolutePath();
            }

            // TODO: try to detect reverse rotation
            if(currentDisplayState.isLandscape()) {
                L.e("MODE IS LANDSCAPE");
                goBro.takeScreenshot(mDeviceLandscapeRotationFactor, fileName);
            } else {
                L.e("MODE IS PORTRAIT");
                goBro.takeScreenshot(mDevicePortraitRotationFactor, fileName);
            }

            setDevicePanelBounds();

            myPicture = ImageIO.read(new File(fileName));

            if (myPicture == null) {
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }

// TODO: affine transform solution
//        if (goBro.getDisplayState().isLandscape()) {     TODO: process device rotation
//            AffineTransform tx = new AffineTransform();
//
//            tx.translate(myPicture.getHeight() / 2, myPicture.getWidth() / 2);
//
//            tx.rotate(Math.PI);
//
//            tx.translate(-myPicture.getWidth() / 2, -myPicture.getHeight() / 2);
//
//            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
//            myPicture = op.filter(myPicture, null);
//        }

        JLabelWithDraw picLabel;

        picLabel = new JLabelWithDraw(new ImageIcon(GraphicsUtil.resize(myPicture, w, h)));

        picLabel.setVisible(true);
        picLabel.setSize(w, h);

        return picLabel;
    }

    public void saveRecordedTest(HistoryType type) {

        if(RecordingManager.getInstance() == null || RecordingManager.getInstance().getCurrentTestCase() == null
                || RecordingManager.getInstance().getCurrentTestCase().isEmpty()) {
            L.a(devicePanelFrame, "Nothing to save");
            return;
        }

        JFrame frame;
        frame = new JFrame("Save test scenario (Please create empty file for Mac before saving)");
        frame.setSize(400, 300);
        JFileChooser jFileChooser1 = new JFileChooser();
        jFileChooser1.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser1.setDialogTitle("Save test scenario (Please create empty file for Mac before saving)");

        int retVal = jFileChooser1.showSaveDialog(frame);

        switch (retVal) {
            case JFileChooser.CANCEL_OPTION:
                System.out.println("Cancel chosen");
                break;
            case JFileChooser.APPROVE_OPTION:
                System.out.println("Path: " + jFileChooser1.getSelectedFile().getAbsolutePath());
                if(type == HistoryType.EMULATOR) {
                    RecordingManager.getInstance().serialize(jFileChooser1.getSelectedFile().getAbsolutePath());
                }
        }
    }

    public void loadTest(HistoryType type) {
        JFrame frame;
        frame = new JFrame("Load test scenario");
        frame.setSize(400, 300);
        JFileChooser jFileChooser1 = new JFileChooser();
        jFileChooser1.setDialogType(JFileChooser.SAVE_DIALOG);
        jFileChooser1.setDialogTitle("Loat test scenario");

        int retVal = jFileChooser1.showOpenDialog(frame);

        switch (retVal) {
            case JFileChooser.CANCEL_OPTION:
                System.out.println("Cancel chosen");
                break;
            case JFileChooser.APPROVE_OPTION:
                System.out.println("Path: " + jFileChooser1.getSelectedFile().getAbsolutePath());
                if(type == HistoryType.EMULATOR) {
                    RecordingManager.getInstance().deserialize(jFileChooser1.getSelectedFile().getAbsolutePath());
                }
                break;
        }
    }

    public void windowOpened(WindowEvent e) {
        System.out.println("windowOpened");
    }

    public void windowClosing(WindowEvent e) {
        if (isGroupControl) {
            System.out.println("IT WAS GROUP CONTROL");
            isGroupControl = false;
        } else {
            System.out.println("IT WAS SINGLE DEVICE CONTROL");
            stopLogcat = true;
            System.exit(0);
        }
    }

    public void windowClosed(WindowEvent e) {
        System.out.println("windowClosed");
    }

    public void windowIconified(WindowEvent e) {
        System.out.println("windowIconified");
    }

    public void windowDeiconified(WindowEvent e) {
        System.out.println("windowDeiconified");
    }

    public void windowActivated(WindowEvent e) {
        System.out.println("windowActivated");
    }

    public void windowDeactivated(WindowEvent e) {
        System.out.println("windowDeactivated");
    }

    protected void applyDoScreenshotsAutomatically() {
        if (mChbxGetScreenshotAfterEachAction.isSelected()) {
            mBtnGetScreenshot.setEnabled(false);
        } else {
            mBtnGetScreenshot.setEnabled(true);
        }
    }

    public void setGoBro(GoBro goBro) {
        this.goBro = goBro;
    }

    private void startLogCatThread() {
        if(!isLogcatAllowed) {
            return;
        }

        if(logcatThread != null) {
            while (logcatThread.isAlive()) {
                logcatThread.interrupt();
            }
        }

        stopLogcat = false;
        logcatThread = new Thread(new LogCatMonitorRunnable());
        logcatThread.start();
    }

    public void setLastXMarker(int lastXMarker) {
        this.lastXMarker = lastXMarker;
    }

    public int getLastXMarker() {
        return lastXMarker;
    }

    public void setLastYMarker(int lastYMarker) {
        this.lastYMarker = lastYMarker;
    }

    public int getLastYMarker() {
        return lastYMarker;
    }

    private class LogCatMonitorRunnable implements Runnable {
        @Override
        public void run() {
            logFile = new File(System.currentTimeMillis()+"_logcat.txt");
            goBro.execShell("logcat -v time", logCatReceiver);
        }
    }

    private static boolean stopLogcat = false;
    private static File logFile;
    private static MultiLineReceiver logCatReceiver = new MultiLineReceiver() {
        @Override
        public void processNewLines(String[] strings) {
            //To change body of implemented methods use File | Settings | File Templates.

            if(logFile == null) {
                L.e("LogFile is null");
                return;
            }

            for(String str: strings) {
                FileUtils.writeText(logFile, str, true);
            }
        }

        @Override
        public boolean isCancelled() {
            return stopLogcat;  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    private class EmulatorMenuBar extends JMenuBar {
        public EmulatorMenuBar() {
            super();
            Font font = new Font("Verdana", Font.PLAIN, 11);
            JMenu testManagementMenu = new JMenu("Test");
            testManagementMenu.setFont(font);

            JMenu testRecordingMenu = new JMenu("Record test");
            testRecordingMenu.setFont(font);
            testManagementMenu.add(testRecordingMenu);

            startRecordingItem = new JMenuItem("Start new recording");
            startRecordingItem.setFont(font);
            startRecordingItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startRecording_Emulator(true);
                }
            });

            continueRecordingItem = new JMenuItem("Continue current recording");
            continueRecordingItem.setFont(font);
            continueRecordingItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startRecording_Emulator(false);
                }
            });

            testRecordingMenu.add(startRecordingItem);
            testRecordingMenu.add(continueRecordingItem);

            stopRecordingItem = new JMenuItem("Stop recording");
            stopRecordingItem.setEnabled(false);
            stopRecordingItem.setFont(font);
            stopRecordingItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stopRecording();
                }
            });
            testRecordingMenu.add(stopRecordingItem);

            JMenu testPlayingMenu = new JMenu("Play test");
            testRecordingMenu.setFont(font);
            testManagementMenu.add(testPlayingMenu);

            playTest = new JMenuItem("Play");
            playTest.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    playTestCase(RecordingManager.getInstance().getCurrentTestCase().getId());
                }
            });

            testPlayingMenu.add(playTest);

            stopPlaying = new JMenuItem("Stop test");
            stopPlaying.setEnabled(false);
            stopPlaying.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(testPlayerThread != null && testPlayerThread.isAlive()) {
                        testPlayerThread.interrupt();
                    }

                    playTest.setEnabled(true);
                    stopPlaying.setEnabled(false);
                }
            });

            testPlayingMenu.add(stopPlaying);

            JMenuItem editTestItem = new JMenuItem("Edit test");
            editTestItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JTestRecordingFrame.getInstance().loadTestCase(RecordingManager.getInstance());
                }
            });
            editTestItem.setFont(font);
            testManagementMenu.add(editTestItem);

            JMenuItem saveTestCaseItem = new JMenuItem("Save test");
            saveTestCaseItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveRecordedTest(HistoryType.EMULATOR);
                }
            });


            saveTestCaseItem.setFont(font);
            testManagementMenu.add(saveTestCaseItem);

            JMenuItem loadTest = new JMenuItem("Load test");
            loadTest.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    loadTest(HistoryType.EMULATOR);
                }
            });
            loadTest.setFont(font);
            testManagementMenu.add(loadTest);

            testManagementMenu.addSeparator();

            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setFont(font);
            testManagementMenu.add(exitItem);

            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            this.add(testManagementMenu);
            JMenu settings = new JMenu("Settings");

            JCheckBoxMenuItem logcatThread = new JCheckBoxMenuItem("Capture logcat");
            logcatThread.setSelected(isLogcatAllowed);
            logcatThread.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem) e.getSource();
                        isLogcatAllowed = jCheckBoxMenuItem.isSelected();
                        if (isScreenshotsAllowed) {
                            startLogCatThread();
                        }
                    }
                }
            });
            settings.add(logcatThread);

            final JCheckBoxMenuItem screenshotsThread = new JCheckBoxMenuItem("Capture screenshots");
            screenshotsThread.setSelected(isScreenshotsAllowed);
            screenshotsThread.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem)e.getSource();
                        isScreenshotsAllowed = jCheckBoxMenuItem.isSelected();
                        if(isScreenshotsAllowed) {
                            startScreenshotsCapturingThread();
                        }
                    }
                }
            });

            settings.add(screenshotsThread);

            JCheckBoxMenuItem alwaysOnTop = new JCheckBoxMenuItem("Always on top");
            alwaysOnTop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof JCheckBoxMenuItem) {
                        JCheckBoxMenuItem jCheckBoxMenuItem = (JCheckBoxMenuItem)e.getSource();
                        devicePanelFrame.setAlwaysOnTop(jCheckBoxMenuItem.isSelected());
                    }
                }
            });
            settings.add(alwaysOnTop);
            this.add(settings);

            final JMenu makeScreenshot = new JMenu("Make a screenshot");
            makeScreenshot.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(e.getSource() instanceof JCheckBoxMenuItem) {
                        makeScreenshot.setEnabled(false);
//
//                        // TODO: try to detect reverse rotation
//                        File test = new File("./user_screen_capture_"+System.currentTimeMillis()+".png").getAbsolutePath()
//                        if(currentDisplayState.isLandscape()) {
//                            L.e("MODE IS LANDSCAPE");
//                            goBro.takeScreenshot(mDeviceLandscapeRotationFactor, );
//                        } else {
//                            L.e("MODE IS PORTRAIT");
//                            goBro.takeScreenshot(mDevicePortraitRotationFactor, fileName);
//                        }
                    }
                }
            });
        }
    }

    public void playTestCase(final long id) {

        if(!RecordingManager.getInstance().getTestCase(id).isEnabled()) {
            L.e("Test case: " + id + " is disabled");
            return;
        }

        testPlayerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RecordingManager.getInstance().setTestPlaying(true);
                playTest.setEnabled(false);
                stopPlaying.setEnabled(true);
                startNewFolder();
                startLogCatThread();
                playEmulatorEventsHistoryXY(id);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                RecordingManager.getInstance().setTestPlaying(false);
                playTest.setEnabled(true);
                stopPlaying.setEnabled(false);
            }
        });

        testPlayerThread.start();
    }

    private void stopRecording() {
        if (RecordingManager.getInstance().isTestRecording()) {
            startRecordingItem.setEnabled(true);
            continueRecordingItem.setEnabled(true);
            stopRecordingItem.setEnabled(false);
            playTest.setEnabled(true);
            stopPlaying.setEnabled(false);
            RecordingManager.getInstance().setTestRecording(false);
            stopLogcat = true;

            RecordingManager.getInstance().normalizeHistory();
            JTestRecordingFrame.getInstance().loadTestCase(RecordingManager.getInstance());
        }
    }
}

