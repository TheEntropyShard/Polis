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
    private final JButton backButton;
    private final JButton forwardButton;
    private final JButton refreshButton;
    private final JTextField uriField;
    private final JButton moreButton;

    public AddressBar(Consumer<String> inputConsumer) {
        super(JToolBar.HORIZONTAL);

        this.setBorder(
            new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIManager.getColor("addressBarBorderColor")),
                new EmptyBorder(4, 2, 4, 2)
            )
        );

        this.backButton = new AddressBarButton("arrow_back_24");
        this.add(this.backButton);

        this.forwardButton = new AddressBarButton("arrow_forward_24");
        this.add(this.forwardButton);

        this.refreshButton = new AddressBarButton("refresh_24");
        this.add(this.refreshButton);

        this.uriField = new JTextField();
        this.uriField.setPreferredSize(new Dimension(0, 36));
        this.uriField.putClientProperty(FlatClientProperties.STYLE_CLASS, "uriTextField");
        this.uriField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter search query or URL");
        this.uriField.setFont(this.uriField.getFont().deriveFont(14.0f));
        this.uriField.addActionListener(e -> inputConsumer.accept(this.uriField.getText()));
        JLabel icon = new JLabel(AddressBar.loadIcon("shield_locked_24"));
        icon.setBorder(new EmptyBorder(0, 8, 0, 4));
        this.uriField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, icon);
        SelectIfNotDraggedListener.install(this.uriField);
        this.add(this.uriField);

        this.moreButton = new AddressBarButton("more_vert_24");
        this.add(this.moreButton);
    }

    @Override
    public void requestFocus() {
        this.uriField.requestFocus();
    }

    private static FlatSVGIcon loadIcon(String name) {
        return new FlatSVGIcon(AddressBar.class.getResource("/icons/" + name + ".svg"))
            .derive(18, 18)
            .setColorFilter(new FlatSVGIcon.ColorFilter(color -> UIManager.getColor("addressBarEnabledButtonColor")));
    }

    public void setCurrentUri(String uri) {
        this.uriField.setText(uri);
    }

    public JButton getBackButton() {
        return this.backButton;
    }

    public JButton getForwardButton() {
        return this.forwardButton;
    }

    public JButton getRefreshButton() {
        return this.refreshButton;
    }

    public JButton getMoreButton() {
        return this.moreButton;
    }
}
