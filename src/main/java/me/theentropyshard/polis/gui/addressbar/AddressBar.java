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
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.function.Consumer;

public class AddressBar extends JToolBar {
    private final JTextField uriField;

    public AddressBar(Consumer<String> inputConsumer) {
        super(JToolBar.HORIZONTAL);

        this.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(4, 2, 4, 2)));

        JButton backButton = new JButton(AddressBar.loadIcon("arrow_back_24"));
        backButton.putClientProperty(FlatClientProperties.STYLE, "toolbar.spacingInsets: 6,8,6,8");
        this.add(backButton);

        JButton forwardButton = new JButton(AddressBar.loadIcon("arrow_forward_24"));
        forwardButton.putClientProperty(FlatClientProperties.STYLE, "toolbar.spacingInsets: 6,8,6,8");
        this.add(forwardButton);

        JButton refreshButton = new JButton(AddressBar.loadIcon("refresh_24"));
        refreshButton.putClientProperty(FlatClientProperties.STYLE, "toolbar.spacingInsets: 6,8,6,8");
        this.add(refreshButton);

        this.uriField = new JTextField();
        this.uriField.setPreferredSize(new Dimension(0, 36));
        JLabel icon = new JLabel(AddressBar.loadIcon("shield_locked_24"));
        icon.setBorder(new EmptyBorder(0, 8, 0, 4));
        this.uriField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);
        this.uriField.putClientProperty(FlatClientProperties.STYLE, """
            arc: 999;
            background: #EDF2FA;
            borderColor: #EDF2FA;
            focusedBackground: #FFFFFF;
            focusColor: #0B57D0;
            focusWidth: 1;
            placeholderForeground: #101010;
            """);
        this.uriField.setFont(this.uriField.getFont().deriveFont(14.0f));
        this.uriField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter search query or URL");
        this.uriField.addActionListener(e -> {
            inputConsumer.accept(this.uriField.getText());
        });
        this.add(this.uriField);

        JButton moreButton = new JButton(AddressBar.loadIcon("more_vert_24"));
        moreButton.putClientProperty(FlatClientProperties.STYLE, "toolbar.spacingInsets: 6,8,6,8");
        this.add(moreButton);
    }

    private static FlatSVGIcon loadIcon(String name) {
        return new FlatSVGIcon(
            AddressBar.class.getResource("/icons/" + name + ".svg")
        ).derive(18, 18)
            .setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.DARK_GRAY.darker()));
    }

    public void setCurrentUri(String uri) {
        this.uriField.setText(uri);
    }
}
