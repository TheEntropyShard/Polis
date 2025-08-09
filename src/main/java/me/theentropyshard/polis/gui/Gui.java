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

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

import me.theentropyshard.polis.gemini.client.GeminiClient;
import me.theentropyshard.polis.gui.emoji.EmojiSupport;
import me.theentropyshard.polis.gui.laf.LightPolisLaf;

public class Gui {
    private final JTabbedPane tabbedPane;

    private final GeminiClient client = new GeminiClient();

    public Gui() {
        Gui.prepare();

        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        this.tabbedPane.setPreferredSize(new Dimension(1280, 720));
        this.tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true);
        this.tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK, (IntConsumer) this::onTabClose);

        this.tabbedPane.setDropTarget(new FileDropTarget(file -> {
            Tab tab = new Tab(this.client);
            tab.loadFromFile(file);
            this.tabbedPane.addTab("Title", tab);
        }));

        this.createEmptyTab();

        JFrame frame = new JFrame("Polis");
        frame.getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        frame.add(this.tabbedPane, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void onTabClose(int index) {
        this.tabbedPane.removeTabAt(index);

        if (this.tabbedPane.getTabCount() == 0) {
            System.exit(0);
        }
    }

    public void createEmptyTab() {
        this.tabbedPane.addTab("Title", new Tab(this.client));
    }

    public void createTab(String url) {
        Tab tab = new Tab(this.client);
        tab.loadFromUrl(url);
        this.tabbedPane.addTab("Title", tab);
    }

    private static void prepare() {
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);

        FontLoader.loadFonts();

        FlatLaf.registerCustomDefaultsSource("themes");

        LightPolisLaf.setup();

        EmojiSupport.init();
    }
}
