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

import org.yahor.gobrotium.utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class ConfigurationManager {
    private static Configuration config = null;
    private static Properties properties;
    private static File phoneBooksFilesRoot;

    public static void init() {

        try {
            loadProperties();
        } catch (IOException e) {
            closeApp("Unable to load configuration file: " + Constants.Configuration.CONFIGURATION_FILE_PATH);
        }

        String android_home = getProperty(Constants.Configuration.ANDROID_HOME);

        File androidHomeFile = new File(android_home, Constants.PathParts.ANDROID_PLATFORM_TOOLS);
        L.i("CHECKING ANDROID HOME: " + androidHomeFile.getPath() + " Exists? " + androidHomeFile.exists());
        closeAppOnTrue(!androidHomeFile.exists(), Constants.Messages.ANDROID_HOME_IS_INVALID);

        config = new Configuration(androidHomeFile.getPath());
        config.acquireDeviceBridge();
        config.getDeviceBridge().listConnectedDevices();
    }

    public static String getProperty(String propertyKey) {
        Enumeration enuKeys = properties.keys();
        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();

            if(!key.equalsIgnoreCase(propertyKey)) continue;

            String value = properties.getProperty(key);

            L.i("GOT PROPERTY: " + value + " FOR: " + propertyKey);

            return value;
        }

        return null;
    }

    private static void loadProperties() throws IOException {
        File configProperties = new File(Constants.Configuration.CONFIGURATION_FILE_PATH);
        FileInputStream fileInput = new FileInputStream(configProperties);
        Properties properties = new Properties();
        properties.load(fileInput);
        fileInput.close();

        setProperties(properties);
    }

    public static Configuration getConfig() {
        if(config == null) {
            L.i("Please call init() first, in other case config is null anyway");
            System.exit(0);
        }
        return config;
    }

    public static void setConfig(Configuration cfg) {
    	config = cfg;
    }

    public static void setDeviceSn(String serialNumber) {
        getConfig().setDeviceSerialNumber(serialNumber);
    }

    public static void setProperties(Properties properties) {
        ConfigurationManager.properties = properties;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void closeApp(String s) {
        L.i(s);
        System.exit(0);
    }

    public static void closeAppOnTrue(boolean condition, String message) {
        if(condition) {
            closeApp(message);
        }
    }
}
