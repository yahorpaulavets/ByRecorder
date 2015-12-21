/*
 * HWApp.java
 */

package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.yahor.gobrotium.GoBro;
import org.yahor.gobrotium.config.ConfigurationManager;

// пнд, 8:00.

/**
 * The main class of the application.
 */
public class ByRecorder extends SingleFrameApplication {

    public static GoBro goBro;
    private GenericDeviceRemoteControl remoteControl;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {

        ConfigurationManager.init();
        goBro = new GoBro(ConfigurationManager.getConfig());
        remoteControl =  new GenericDeviceRemoteControl(goBro);
        show(remoteControl);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ByRecorder
     */
    public static ByRecorder getApplication() {
        return Application.getInstance(ByRecorder.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(ByRecorder.class, args);
    }
}
