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
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;

import me.theentropyshard.polis.gemini.client.GeminiClient;
import me.theentropyshard.polis.gemini.client.GeminiRequest;
import me.theentropyshard.polis.gemini.client.GeminiResponse;
import me.theentropyshard.polis.gemini.gemtext.GemtextParser;
import me.theentropyshard.polis.gui.addressbar.AddressBar;
import me.theentropyshard.polis.gui.gemtext.GemtextPane;

public class Tab extends JPanel {
    private final GeminiClient client;

    private final AddressBar addressBar;
    private final GemtextPane pane;

    private URI currentUri;

    public Tab(String url, GeminiClient client) {
        this.client = client;

        this.addressBar = new AddressBar(input -> {
            if (!input.startsWith("gemini://")) {
                input = "gemini://" + input;
            }

            this.currentUri = URI.create(input);

            this.reload();
        });

        this.pane = new GemtextPane(link -> {
            this.currentUri = this.currentUri.resolve(link);

            this.reload();
        });

        this.currentUri = URI.create(url);

        JScrollPane scrollPane = new JScrollPane(
            this.pane,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setUI(new FlatSmoothScrollPaneUI());

        this.setLayout(new BorderLayout());

        this.add(this.addressBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        this.load();
    }

    public void load(String location, InputStream inputStream) throws IOException {
        this.addressBar.setCurrentUri(location);

        new GemtextParser().parse(inputStream, this.pane::writeElement);
    }

    public void loadFromFile(File file) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Tab.this.load(file.toURI().toString(), Files.newInputStream(file.toPath()));

                return null;
            }
        }.execute();
    }

    private void load() {
        this.updateAddressBar();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (GeminiResponse response = Tab.this.client.send(new GeminiRequest(Tab.this.currentUri.toASCIIString()))) {
                    Tab.this.load(Tab.this.currentUri.toASCIIString(), response.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    private void reload() {
        this.pane.clear();

        this.load();
    }

    public void updateAddressBar() {
        this.addressBar.setCurrentUri(this.currentUri.toString());
    }
}
