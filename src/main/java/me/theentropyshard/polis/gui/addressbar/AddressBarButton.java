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

public class AddressBarButton extends JButton {
    private final String iconName;

    public AddressBarButton(String iconName) {
        this.iconName = iconName;

        this.setIcon(AddressBarButton.loadIcon(this.iconName, true));
        this.putClientProperty(FlatClientProperties.STYLE, "toolbar.spacingInsets: 6,8,6,8");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        this.setIcon(AddressBarButton.loadIcon(this.iconName, enabled));
    }

    private static FlatSVGIcon loadIcon(String name, boolean enabled) {
        return new FlatSVGIcon(AddressBar.class.getResource("/icons/" + name + ".svg"))
            .derive(18, 18)
            .setColorFilter(new FlatSVGIcon.ColorFilter(color -> {
                return enabled ?
                    UIManager.getColor("addressBarEnabledButtonColor") :
                    UIManager.getColor("addressBarDisabledButtonColor");
            }));
    }
}
