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

import org.yahor.gobrotium.helpers.XmlHelper;
import org.yahor.gobrotium.helpers.XmlHierarchyConstants;
import org.w3c.dom.Node;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewBounds implements Serializable {
    private int upperLeftX;
    private int upperLeftY;
    private int bottomRightX;
    private int bottomRightY;
    private String stringBoundsValue;

    public ViewBounds(Node xmlNode) {
        String bounds = XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.BOUNDS_ATTR, xmlNode);
        setStringBoundsValue(bounds);
        parseBounds(bounds);
    }

    private void parseBounds(String bounds) {
        Pattern p = Pattern.compile("\\d+");

        Matcher matcher = p.matcher(bounds);

        String[] values = new String[4];

        int startIndex = 0;
        int counter = 0;
        while(matcher.find(startIndex)) {
            values[counter] = bounds.substring(matcher.start(), matcher.end());

            startIndex = matcher.end();
            counter++;
        }

        this.upperLeftX = Integer.parseInt(values[0]);
        this.upperLeftY = Integer.parseInt(values[1]);
        this.bottomRightX = Integer.parseInt(values[2]);
        this.bottomRightY = Integer.parseInt(values[3]);
    }

    public int getUpperLeftX() {
        return upperLeftX;
    }

    public int getUpperLeftY() {
        return upperLeftY;
    }

    public int getBottomRightX() {
        return bottomRightX;
    }

    public int getBottomRightY() {
        return bottomRightY;
    }

    public void setStringBoundsValue(String stringBoundsValue) {
        this.stringBoundsValue = stringBoundsValue;
    }

    public String getStringBoundsValue() {
        return stringBoundsValue;
    }

    public String toString() {
        return getStringBoundsValue();
    }
}
