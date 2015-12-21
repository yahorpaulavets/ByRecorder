package org.yahor.gobrotium.utils;

import org.yahor.gobrotium.config.Constants;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

public class FileUtils {
    public static String loadTextFile(String path, boolean separateLines) {
        StringBuilder contents = new StringBuilder();

        try {
            BufferedReader input = new BufferedReader(new FileReader(new File(path)));
            try {
                String line; //not declared within while loop

                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    if(separateLines) {
                        contents.append(System.getProperty("line.separator"));
                    }
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }

    public static String getProperty(String android_home) {

        File configProperties = new File(Constants.Configuration.CONFIGURATION_FILE_PATH);
        L.i("PWD CONFIG.INI: " + configProperties.getAbsolutePath());

        try {
            FileInputStream fileInput = new FileInputStream(configProperties);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            Enumeration enuKeys = properties.keys();
            while (enuKeys.hasMoreElements()) {
                String key = (String) enuKeys.nextElement();
                String value = properties.getProperty(key);
                L.d(key + ": " + value);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static void writeText(File file, String logEntry, boolean append) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
            out.println(logEntry);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
