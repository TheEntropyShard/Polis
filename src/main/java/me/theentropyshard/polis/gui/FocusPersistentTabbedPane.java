/*
 * Polis - https://github.com/TheEntropyShard/Polis
 * Copyright (C) 2025 TheEntropyShard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.polis.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

// https://stackoverflow.com/a/6748242/19857533
public class FocusPersistentTabbedPane extends JTabbedPane implements ChangeListener, PropertyChangeListener {
    private final Map<Integer, Object> tabs;

    public FocusPersistentTabbedPane(int tabPlacement) {
        super(tabPlacement);

        this.tabs = new HashMap<>();

        this.addChangeListener(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if ("permanentFocusOwner".equals(e.getPropertyName())) {
            Object value = e.getNewValue();
            if (value != null) {
                this.tabs.put(this.getComponentAt(this.getSelectedIndex()).hashCode(), value);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object value = this.tabs.get(this.getComponentAt(this.getSelectedIndex()).hashCode());
        if (value != null) {
            ((Component) value).requestFocusInWindow();
        }
    }
}
