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

public class Constants {
    public static class DumpSysWindowPrefixes {
        public static final String DISPLAY = "init=";
        public static final String CURRENT_FOCUS = "mCurrentFocus=Window";
        public static final String SCREEN_STATE = "mScreenOnEarly=";
        public static final CharSequence KEY_GUARD_MARKER = "Keyguard paused=false";
        public static final CharSequence SCREEN_DENSITY = "dpi";
    }

    public static class Configuration {
        public static final String CONFIGURATION_FILE_PATH = "config.properties";
        public static final String ANDROID_HOME = "ANDROID_HOME";
        public static final String AUDIO_HOME = "AUDIO_HOME";
        public static final String PHONE_BOOKS_HOME = "PHONEBOOK_HOME";
        public static final String GOOGLE_USER = "GOOGLE_USERNAME";
        public static final String GOOGLE_PASSWORD = "GOOGLE_PASSWORD";
        public static final String GOOGLE_CALENDAR_ID = "GOOGLE_CALENDAR_ID";
        public static int VERBOSE_LEVEL = 4;
        public static boolean TESTS_OUTPUT_ENABLED = false;
        public static boolean DEBUG_ENABLED = false;
    }

    public static class Calendars {
        public static final String INTENT_GOOGLE_CALENDAR ="am start -a android.intent.action.LAUNCHER -n com.google.android.calendar/com.android.calendar.AllInOneActivity";
        public static final String INTENT_S_PLANNER ="am start -a android.intent.action.LAUNCHER -n com.android.calendar/com.android.calendar.AllInOneActivity";

        public enum SupportedCalendars {
            GOOGLE_CALENDAR,
            S_PLANNER
        }
    }

    public static class Messages {
        public static final String ANDROID_HOME_IS_INVALID = "Please set Environment variable ANDROID_HOME pointed to desired Android SDK root folder";
        public static final String AUDIO_HOME_INVALID = "Please set point to audio files root folder.";
    }

    public static class PathParts {
        public static final String ANDROID_PLATFORM_TOOLS = "platform-tools";
        public static final String WINDOWS_ADB_EXE = "adb.exe";
        public static final String LINUX_ADB = "adb";
    }

    public static class SHELL_OUTPUT {

        public static final String HIERARCHY_IS_DUMPED_PATH = "UI hierchary dumped to:";
    }

    public static class AndroidCommands {
        public static final String IMPORT_CONTACTS_FROM_VCF = "shell am start -a android.intent.action.VIEW -n com.android.contacts/.vcard.ImportVCardActivity -d file:/mnt/sdcard/";
        public static final String CLEAR_CONTACTS = "shell pm clear com.android.providers.contacts";
    }

    public static class AdbCommands {
        public static final String PUSH_FILE_TO_SDCARD = "push %s /sdcard/";
    }

    public static class Prerequisites {
        public static final String CLEAR_CONTACTS = "clear_contacts";
    }

    public static class View {
        public static final String TEXT_VIEW = "TextView";
    }
}
