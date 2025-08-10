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

import javax.swing.text.JTextComponent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectIfNotDraggedListener extends MouseAdapter {
    private final JTextComponent textComponent;

    private boolean canSelect = true;

    private SelectIfNotDraggedListener(JTextComponent textComponent) {
        this.textComponent = textComponent;

        textComponent.addMouseListener(this);
        textComponent.addMouseMotionListener(this);

        textComponent.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                SelectIfNotDraggedListener.this.canSelect = true;
            }
        });
    }

    public static void install(JTextComponent textComponent) {
        new SelectIfNotDraggedListener(textComponent);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.canSelect) {
            this.textComponent.selectAll();

            this.canSelect = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.canSelect = false;
    }
}