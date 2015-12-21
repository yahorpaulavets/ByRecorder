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

import org.yahor.gobrotium.utils.L;
import org.yahor.gobrotium.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShellManager {
    public static String executeAdbCommand(String command, String adbLocation, String deviceSn) {

        String res = "";
        String _deviceSn = deviceSn;
        try {

            if (!StringUtils.isNullOrEmpty(deviceSn)) {
                _deviceSn = " -s " + _deviceSn + " ";
            } else {
                _deviceSn = "";
            }

            String cmd = adbLocation + _deviceSn + command;

//            L.d("CMD: " + cmd);

            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                res += line + "\n";
            }

            in.close();

            p.destroy();
        } catch (Exception ex) {
            Logger.getLogger("ShellManager").log(Level.SEVERE, null, ex);
        }

        return res;
    }

    public static String executeServerShellCommand(String command) {
        String res = "";
        try {
            L.i("Shell command: " + command);

            Process p = Runtime.getRuntime().exec(command);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));

            String line;

            while ((line = in.readLine()) != null) {
                res += line;
            }

            p.waitFor();
            p.destroy();
        } catch (Exception ex) {
            Logger.getLogger("ShellManager").log(Level.SEVERE, null, ex);
        }

        return res;
    }
}
