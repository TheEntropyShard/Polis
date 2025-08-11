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

package me.theentropyshard.polis.gui.addressbar;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UriField extends JTextField {
    public UriField() {
        SelectIfNotDraggedListener.install(this);

        this.setFont(this.getFont().deriveFont(14.0f));
        this.setPreferredSize(new Dimension(0, 36));
        this.putClientProperty(FlatClientProperties.STYLE_CLASS, "uriTextField");
        this.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter search query or URL");

        JLabel icon = new JLabel(AddressBar.loadIcon("shield_locked_24"));
        icon.setBorder(new EmptyBorder(0, 8, 0, 4));
        this.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);
    }

    public void focus() {
        this.requestFocus();
        this.selectAll();
    }
}
