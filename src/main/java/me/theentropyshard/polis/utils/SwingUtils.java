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

package me.theentropyshard.polis.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class SwingUtils {
    public static <T, V> SwingWorker<T, V> createWorker(Runnable runnable) {
        return new SwingWorker<>() {
            @Override
            protected T doInBackground() {
                runnable.run();

                return null;
            }
        };
    }

    public static void centerWindow(Window window, int screen) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allDevices = env.getScreenDevices();

        if (screen < 0 || screen >= allDevices.length) {
            screen = 0;
        }

        Rectangle bounds = allDevices[screen].getDefaultConfiguration().getBounds();
        window.setLocation(
            ((bounds.width - window.getWidth()) / 2) + bounds.x,
            ((bounds.height - window.getHeight()) / 2) + bounds.y
        );
    }

    public static void createAction(JComponent component, String key, KeyStroke keyStroke, ActionListener listener) {
        component.getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.actionPerformed(e);
            }
        });

        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
    }

    private SwingUtils() {
        throw new UnsupportedOperationException();
    }
}
