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

import java.lang.reflect.Field;

public class AndroidKeyEvent {
    public static final int KEY_CODE_UNKNOWN = 0;
//    public static final int KEY_CODE_MENU = 1;   ??
    public static final int KEY_CODE_SOFT_RIGHT = 2;
    public static final int KEY_CODE_HOME = 3;
    public static final int KEY_CODE_BACK = 4;
    public static final int KEY_CODE_CALL = 5;
    public static final int KEY_CODE_END_CALL = 6;
    public static final int KEY_CODE_0 = 7;
    public static final int KEY_CODE_1 = 8;
    public static final int KEY_CODE_2 = 9;
    public static final int KEY_CODE_3 = 10;
    public static final int KEY_CODE_4 = 11;
    public static final int KEY_CODE_5 = 12;
    public static final int KEY_CODE_6 = 13;
    public static final int KEY_CODE_7 = 14;
    public static final int KEY_CODE_8 = 15;
    public static final int KEY_CODE_9 = 16;
    public static final int KEY_CODE_STAR = 17;
    public static final int KEY_CODE_POUND = 18;
    public static final int KEY_CODE_DPAD_UP = 19;

    public static final int KEY_CODE_DPAD_DOWN = 20;
    public static final int KEY_CODE_DPAD_LEFT = 21;
    public static final int KEY_CODE_DPAD_RIGHT = 22;
    public static final int KEY_CODE_DPAD_CENTER = 23;
    public static final int KEY_CODE_VOLUME_UP = 24;
    public static final int KEY_CODE_VOLUME_DOWN = 25;
    public static final int KEY_CODE_POWER = 26;
    public static final int KEY_CODE_CAMERA = 27;
    public static final int KEY_CODE_CLEAR = 28;
    public static final int KEY_CODE_A = 29;
    public static final int KEY_CODE_B = 30;
    public static final int KEY_CODE_C = 31;
    public static final int KEY_CODE_D = 32;
    public static final int KEY_CODE_E = 33;
    public static final int KEY_CODE_F = 34;
    public static final int KEY_CODE_G = 35;
    public static final int KEY_CODE_H = 36;
    public static final int KEY_CODE_I = 37;
    public static final int KEY_CODE_J = 38;
    public static final int KEY_CODE_K = 39;
    public static final int KEY_CODE_L = 40;
    public static final int KEY_CODE_M = 41;
    public static final int KEY_CODE_N = 42;
    public static final int KEY_CODE_O = 43;
    public static final int KEY_CODE_P = 44;
    public static final int KEY_CODE_Q = 45;
    public static final int KEY_CODE_R = 46;
    public static final int KEY_CODE_S = 47;
    public static final int KEY_CODE_T = 48;
    public static final int KEY_CODE_U = 49;
    public static final int KEY_CODE_V = 50;
    public static final int KEY_CODE_W = 51;
    public static final int KEY_CODE_X = 52;
    public static final int KEY_CODE_Y = 53;
    public static final int KEY_CODE_Z = 54;
    public static final int KEY_CODE_COMMA = 55;
    public static final int KEY_CODE_PERIOD = 56;
    public static final int KEY_CODE_ALT_LEFT = 57;
    public static final int KEY_CODE_ALT_RIGHT = 58;
    public static final int KEY_CODE_SHIFT_LEFT = 59;
    public static final int KEY_CODE_SHIFT_RIGHT = 60;
    public static final int KEY_CODE_TAB = 61;
    public static final int KEY_CODE_SPACE = 62;
    public static final int KEY_CODE_SYM = 63;
    public static final int KEY_CODE_EXPLORER = 64;
    public static final int KEY_CODE_ENVELOPE = 65;
    public static final int KEY_CODE_ENTER = 66;
    public static final int KEY_CODE_DEL = 67;
    public static final int KEY_CODE_GRAVE = 68;
    public static final int KEY_CODE_MINUS = 69;
    public static final int KEY_CODE_EQUALS = 70;
    public static final int KEY_CODE_LEFT_BRACKET = 71;
    public static final int KEY_CODE_RIGHT_BRACKET = 72;
    public static final int KEY_CODE_BACK_SLASH = 73;
    public static final int KEY_CODE_SEMICOLON = 74;
    public static final int KEY_CODE_APOSTROPHE = 75;
    public static final int KEY_CODE_SLASH = 76;
    public static final int KEY_CODE_AT = 77;
    public static final int KEY_CODE_NUM = 78;
    public static final int KEY_CODE_HEADSETHOOK = 79;
    public static final int KEY_CODE_FOCUS = 80;
    public static final int KEY_CODE_PLUS = 81;
    public static final int KEY_CODE_MENU = 82;
    public static final int KEY_CODE_NOTIFICATION = 83;
    public static final int KEY_CODE_SEARCH = 84;
    public static final int KEY_CODE_TAG_LAST = 85;

    public static String getNameByCode(int code) {
        for(Field f: AndroidKeyEvent.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                int codeToTest  = f.getInt(f);
                if(code == codeToTest) {
                    return f.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return "unable to decode value: " + code;
    }
}
