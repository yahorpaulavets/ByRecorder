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

package org.yahor.gobrotium.helpers;

import org.w3c.dom.Node;

public class XmlHelper {

    public static String getAttributeValue(String attributeName, Node xmlNode) {
        if(xmlNode == null) return null;

        if(xmlNode.getAttributes() == null) return null;

        if(xmlNode.getAttributes().getLength() == 0) return null;

        Node n = xmlNode.getAttributes().getNamedItem(attributeName);

        if(n == null) return null;

        return n.getTextContent();
    }
}
