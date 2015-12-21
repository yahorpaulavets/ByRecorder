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

import org.yahor.gobrotium.ViewLayerPosition;
import org.yahor.gobrotium.helpers.XmlHelper;
import org.yahor.gobrotium.helpers.XmlHierarchyConstants;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Abstraction for UiAutomator node:
 * <node index="0" text="" class="android.widget.FrameLayout" package="android" content-desc=""
 * checkable="false" checked="false" clickable="false" enabled="true" focusable="false"
 * focused="false" scrollable="false" long-clickable="false" password="false" selected="false" bounds="[0,38][480,800]">
 */
public class View implements Serializable {
    private int index;
    private String text;
    private String viewClass;
    private String viewPackage;
    private String viewResourceId;
    private String contentDescription;
    private boolean checkable;
    private boolean checked;
    private boolean clickable;
    private boolean enabled;
    private boolean focusable;
    private boolean focused;
    private boolean scrollable;
    private boolean longClickable;
    private boolean passwordSecure;
    private boolean selected;
    private ViewBounds bounds;
    private Point viewCenterXy;
    private Node relatedNode;
    private boolean hasResourceId;
    private ViewLayerPosition viewLayerPosition;
    private int instanceIndex;
    private boolean isParent;
    private View parentView;
    public ArrayList<View> children = null;

    public View(Node xmlNode) {
        parseIndex(xmlNode);
        parseText(xmlNode);
        parseViewClass(xmlNode);
        parseViewPackage(xmlNode);
        parseContentDescription(xmlNode);
        parseResourceId(xmlNode);

        setCheckable(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.CHECKABLE_ATTR));
        setChecked(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.CHECKED_ATTR));
        setClickable(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.CLICKABLE_ATTR));
        setEnabled(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.ENABLED_ATTR));
        setFocusable(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.FOCUSABLE_ATTR));
        setFocused(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.FOCUSED_ATTR));
        setScrollable(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.SCROLLABLE_ATTR));
        setLongClickable(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.LONG_CLICKABLE_ATTR));
        setPasswordSecure(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.PASSWORD_SECURE_ATTR));
        setSelected(parseBoolean(xmlNode, XmlHierarchyConstants.NodeAttributeNames.SELECTED_ATTR));

        setBounds(new ViewBounds(xmlNode));
    }

    private void parseResourceId(Node xmlNode) {
        setViewResourceId(XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.RESOURCE_ID_ATTR, xmlNode));
    }

    private boolean parseBoolean(Node xmlNode, String checkableAttr) {
        String result = XmlHelper.getAttributeValue(checkableAttr, xmlNode);
        return !(result == null || result.length() == 0) && Boolean.parseBoolean(result);
    }

    private void parseContentDescription(Node xmlNode) {
        setContentDescription(XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.CONTENT_DESCRIPTION_ATTR, xmlNode));
    }

    private void parseViewPackage(Node xmlNode) {
        setViewPackage(XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.VIEW_PACKAGE_ATTR, xmlNode));
    }

    private void parseViewClass(Node xmlNode) {
        setViewClass(XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.VIEW_CLASS_ATTR, xmlNode));
    }

    private void parseText(Node xmlNode) {
        setText(XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.TEXT_ATTR, xmlNode));
    }

    private void parseIndex(Node xmlNode) {
        String index = XmlHelper.getAttributeValue(XmlHierarchyConstants.NodeAttributeNames.INDEX_ATTR, xmlNode);
        setIndex(Integer.parseInt(index));
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getViewClass() {
        return viewClass;
    }

    public void setViewClass(String viewClass) {
        this.viewClass = viewClass;
    }

    public String getViewPackage() {
        return viewPackage;
    }

    public void setViewPackage(String viewPackage) {
        this.viewPackage = viewPackage;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFocusable() {
        return focusable;
    }

    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isLongClickable() {
        return longClickable;
    }

    public void setLongClickable(boolean longClickable) {
        this.longClickable = longClickable;
    }

    public boolean isPasswordSecure() {
        return passwordSecure;
    }

    public void setPasswordSecure(boolean passwordSecure) {
        this.passwordSecure = passwordSecure;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ViewBounds getBounds() {
        return bounds;
    }

    public void setBounds(ViewBounds bounds) {
        this.bounds = bounds;
        setViewCenterXy(computeViewCenter(bounds));
    }

    private Point computeViewCenter(ViewBounds bounds) {
        int approximateDeltaX = (bounds.getBottomRightX() - bounds.getUpperLeftX()) / 2;
        int approximateDeltaY = (bounds.getBottomRightY() - bounds.getUpperLeftY()) / 2;

        Point p = new Point(bounds.getUpperLeftX() + approximateDeltaX,
                bounds.getUpperLeftY() + approximateDeltaY);

        return p;
    }

    public String toString() {
        return String.format(
                "index:%d;" +
                        " text:%s;" +
                        " class:%s" +
                        " package:%s;"+
                        " content-desc:%s;"+
                        " resource-id:%s" +
                        " checkable:%s;"+
                        " checked:%s;"+
                        " clickable:%s;"+
                        " enabled:%s;"+
                        " focusable:%s;"+
                        " focused:%s;"+
                        " scrollable:%s;"+
                        " long-clickable:%s;"+
                        " password:%s;"+
                        " selected:%s;"+
                        " bounds:%s;"
                ,
                getIndex(),
                getText(),
                getViewClass(),
                getViewPackage(),
                getContentDescription(),
                getViewResourceId(),
                isCheckable()+"",
                isChecked()+"",
                isClickable()+"",
                isEnabled()+"",
                isFocusable()+"",
                isFocused()+"",
                isScrollable()+"",
                isLongClickable()+"",
                isPasswordSecure()+"",
                isSelected()+"",
                getBounds().toString()
        );
    }

    public Point getViewCenterXy() {
        return viewCenterXy;
    }

    public void setViewCenterXy(Point viewCenterXy) {
        this.viewCenterXy = viewCenterXy;
    }

    public String getViewResourceId() {
        return viewResourceId;
    }

    public void setViewResourceId(String viewResourceId) {
        if(viewResourceId != null && viewResourceId.length() > 0) {
            setHasResourceId(true);
        }
        this.viewResourceId = viewResourceId;
    }

    public boolean hasResourceId() {
        return hasResourceId;
    }

    public void setHasResourceId(boolean hasResourceId) {
        this.hasResourceId = hasResourceId;
    }

    public void setViewLayerPosition(ViewLayerPosition viewLayerPosition) {
        this.viewLayerPosition = viewLayerPosition;
    }

    public ViewLayerPosition getViewLayerPosition() {
        return viewLayerPosition;
    }

    public void setInstanceIndex(int instanceIndex) {
        this.instanceIndex = instanceIndex;
    }

    public int getInstanceIndex() {
        return instanceIndex;
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    public View getParentView() {
        return parentView;
    }

    public void addChild(View view) {
        if(children == null) {
            children = new ArrayList<View>(10);
        }

        children.add(view);
    }

    public boolean hasChildren() {
        return !(children == null || children.isEmpty());
    }

    public View findChild(String s) {
        if(children == null) return null;

        for(View v: children) {
            String resourceId = v.getViewResourceId();
            if(resourceId == null) { continue; }
            if(resourceId.equalsIgnoreCase(s)) {
                return v;
            }

            if(v.hasChildren()) {
                View found = v.findChild(s);
                if(found != null) return found;
            }
        }

        return null;
    }
}
