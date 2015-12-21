package org.bntu.masterscourse.ypaulavets.byrecorder;

import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.ChimpManager;
import com.android.chimpchat.core.By;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.IChimpView;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;
import com.android.ddmlib.DdmPreferences;
import org.yahor.gobrotium.utils.FileUtils;
import org.yahor.gobrotium.utils.L;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.TreeMap;

public class Test {
    private static ChimpChat mChimpchat = null;
    private static IChimpDevice mDevice = null;
    private static ChimpManager test;
    private static Collection<String> it = null;

    public static void convert(String line) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("./test.out", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
                L.i("STRING: " + line);

                if (line.trim().length() == 0) {
                    return;
                }

                String[] data = line.split("\\s");
                String type = data[0];
                String code = data[1];
                String value = data[2];

                Integer dummy1 = Integer.parseInt("0", 16);
                Short typeShort = Short.parseShort(type, 16);
                Short codeShort = Short.parseShort(code, 16);
                Long valueLong = Long.parseLong(value, 16);

                try {

                    byte[] bytes = ByteBuffer.allocate(4).putInt(dummy1).array();
                    fileOutputStream.write(bytes);

                    bytes = ByteBuffer.allocate(4).putInt(dummy1).array();
                    fileOutputStream.write(bytes);

                    int test = (int)typeShort;
                    test = test << 8;
                    typeShort = (short) test;
                    bytes = ByteBuffer.allocate(2).putShort(typeShort).array();
                    fileOutputStream.write(bytes);

                    test = (int)codeShort;
                    test = test << 8;
                    codeShort = (short) test;
                    bytes = ByteBuffer.allocate(2).putShort(codeShort).array();
                    fileOutputStream.write(bytes);

                    valueLong = valueLong << 32;
                    bytes = ByteBuffer.allocate(8).putLong(valueLong).array();
                    byte[] newArBytes1 = new byte[4];
                    for(int i = 0; i< 4; i++) {
                        newArBytes1[(newArBytes1.length-1)-i] = bytes[i];
                    }

                    fileOutputStream.write(newArBytes1,0,4);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }  finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        DdmPreferences.setTimeOut(300000);

        TreeMap<String, String> options = new TreeMap<String, String>();
        options.put("backend", "adb");
        options.put("adbLocation", "D:\\android\\platform-tools\\adb.exe");
        mChimpchat = ChimpChat.getInstance(options);
        mDevice = mChimpchat.waitForConnection(Integer.MAX_VALUE, ".*");
        mDevice.wake();
        try {
           it =  mDevice.getViewIdList();

           for(String s: it) {
                System.out.println(s + " : ");
                try {
                    IChimpView v = mDevice.getView(By.id(s));
                    System.out.println(v);
                    System.out.println(v.getViewClass() + "  : " );
                    if (v.getViewClass().toString() == "TextView") {
                        System.out.print(v.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            for(String str: mDevice.getPropertyList()) {
                System.out.println("PROP: " + str);
            }
            test = mDevice.getManager();//getRootView();//getHierarchyViewer();
            System.out.println(test);
            System.out.println(test.getRootView().getViewClass());


        } catch (Exception e) {
            e.printStackTrace();

        }

        mChimpchat.shutdown();




//        String[] file = FileUtils.loadTextFile("C:\\Users\\admin\\test.in", true).split("\\n");
//
//        FileOutputStream fileOutputStream = null;
//        try {
//            fileOutputStream = new FileOutputStream("./test.out", true);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        try {
//            for (String fileStr : file) {
//                L.i("STRING: " + fileStr);
//
//                if (fileStr.trim().length() == 0) {
//                    continue;
//                }
//
//                String[] data = fileStr.split("\\s");
//                String type = data[0];
//                String code = data[1];
//                String value = data[2];
//
//                Integer dummy1 = Integer.parseInt("0", 16);
//                Integer dummy2 = Integer.parseInt("0", 16);
//                Short typeShort = Short.parseShort(type, 16);
//                Short codeShort = Short.parseShort(code, 16);
////                Integer valueInt = -1;
////                try {
////                    valueInt = Integer.parseInt(value, 16);
////                } catch (NumberFormatException i) {
////
////                }
//
//                Long valueLong = Long.parseLong(value, 16);
//
//                try {
//
//                    byte[] bytes = ByteBuffer.allocate(4).putInt(dummy1).array();
//                    fileOutputStream.write(bytes);
//
//                    bytes = ByteBuffer.allocate(4).putInt(dummy1).array();
//                    fileOutputStream.write(bytes);
//
//                    ///
//
//                    int test = (int)typeShort;
//                    test = test << 8;
//                    typeShort = (short) test;
//                    bytes = ByteBuffer.allocate(2).putShort(typeShort).array();
//                    fileOutputStream.write(bytes);
//
//                    test = (int)codeShort;
//                    test = test << 8;
//                    codeShort = (short) test;
//                    bytes = ByteBuffer.allocate(2).putShort(codeShort).array();
//                    fileOutputStream.write(bytes);
//
//                    valueLong = valueLong << 32;
//                    bytes = ByteBuffer.allocate(8).putLong(valueLong).array();
////                    bytes = reverse(bytes);
//                    byte[] newArBytes1 = new byte[4];
//                    for(int i = 0; i< 4; i++) {
//                        newArBytes1[(newArBytes1.length-1)-i] = bytes[i];
//                    }
//
//                    fileOutputStream.write(newArBytes1,0,4);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }  finally {
//            try {
//                fileOutputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private static byte[] reverse(byte[] bytes) {
        byte[] newArray = new byte[4];

        newArray[0] = bytes[3];
        newArray[0] = bytes[2];
        newArray[0] = bytes[1];
        newArray[0] = bytes[0];

        return newArray;
    }

    public static int sizeof(Class dataType)
    {
        if (dataType == null) throw new NullPointerException();

        if (dataType == int.class    || dataType == Integer.class)   return 4;
        if (dataType == short.class  || dataType == Short.class)     return 2;
        if (dataType == byte.class   || dataType == Byte.class)      return 1;
        if (dataType == char.class   || dataType == Character.class) return 2;
        if (dataType == long.class   || dataType == Long.class)      return 8;
        if (dataType == float.class  || dataType == Float.class)     return 4;
        if (dataType == double.class || dataType == Double.class)    return 8;

        return 4; // 32-bit memory pointer...
        // (I'm not sure how this works on a 64-bit OS)
    }
}
