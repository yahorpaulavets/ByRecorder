package org.bntu.masterscourse.ypaulavets.byrecorder;

import org.yahor.gobrotium.utils.L;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProcessBuilderTest {
    private ProcessBuilder builder;
    private Process adb;
    private static final byte[] LS = "\n".getBytes();

    private OutputStream processInput;
    private InputStream processOutput;

    private Thread t;

    /**
     * Starts the shell
     */
    public void start() throws IOException  {
        builder = new ProcessBuilder("adb", "shell");
        adb = builder.start();

        // reads from the process output
        processInput = adb.getOutputStream();

        // sends to process's input
        processOutput = adb.getInputStream();

        // thread that reads process's output and prints it to system.out
        t = new Thread() {
            public void run() {
                try   {
                    int c = 0;
                    byte[] buffer = new byte[2048];
                    while((c = processOutput.read(buffer)) != -1) {
                        System.out.write(buffer, 0, c);
                    }
                }catch(Exception e)  {}
            }
        };
        t.start();
    }

    /**
     * Stop the shell;
     */
    public void stop()   {
        try   {
            if(processOutput != null && t != null) {
                this.execCommand("exit");
                processOutput.close();
            }
        }catch(Exception ignore)  {}
    }

    /**
     * Executes a command on the shell
     * @param adbCommand the command line.
     * e.g. "am start -a android.intent.action.MAIN -n com.q.me.fui.activity/.InitActivity"
     */
    public void execCommand(String adbCommand) throws IOException {
        processInput.write(adbCommand.getBytes());
        processInput.write(LS);
        processInput.flush();
    }

    public static void main(ArrayList<TouchTestCaseItem> args) throws Exception  {
        ProcessBuilderTest shell = new ProcessBuilderTest();
        shell.start();

        long last = 0;
        for(TouchTestCaseItem arg : args)  {
            String historyItem = arg.toEventString();

            if(last > 0 && (arg.getOccurred() - last) > 100 ) {
                long pause = (arg.getOccurred() - last);
                L.i("PAUSE: " + pause);
//                Thread.sleep(pause);
                shell.execCommand("sleep " + pause/1000);
            }

            shell.execCommand(historyItem);
            L.i(historyItem);

            last = arg.getOccurred();
        }

        shell.stop();
    }
}
