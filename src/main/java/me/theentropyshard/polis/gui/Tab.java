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
import java.net.UnknownHostException;
import java.nio.file.Files;

import me.theentropyshard.polis.gemini.client.GeminiClient;
import me.theentropyshard.polis.gemini.client.GeminiRequest;
import me.theentropyshard.polis.gemini.client.GeminiResponse;
import me.theentropyshard.polis.gemini.gemtext.GemtextParser;
import me.theentropyshard.polis.gemini.gemtext.document.GemtextH1Element;
import me.theentropyshard.polis.gemini.gemtext.document.GemtextParagraphElement;
import me.theentropyshard.polis.gui.addressbar.AddressBar;
import me.theentropyshard.polis.gui.gemtext.GemtextPane;

public class Tab extends JPanel {
    private final GeminiClient client;

    private final AddressBar addressBar;
    private final GemtextPane pane;

    private URI currentUri;

    private SwingWorker<Void, Void> currentWorker;

    public Tab(GeminiClient client) {
        this.client = client;

        this.addressBar = new AddressBar(input -> {
            if (!input.startsWith("gemini://")) {
                input = "gemini://" + input;
            }

            this.currentUri = URI.create(input);

            this.reload();
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem savePageItem = new JMenuItem("Save page");
        popupMenu.add(savePageItem);

        this.addressBar.getMoreButton().addActionListener(e -> {
            JButton b = (JButton) e.getSource();

            popupMenu.show(this, this.getParent().getPreferredSize().width - popupMenu.getPreferredSize().width, b.getY() + b.getPreferredSize().height);
        });

        this.pane = new GemtextPane(link -> {
            this.currentUri = this.currentUri.resolve(link);

            this.reload();
        });

        savePageItem.addActionListener(e -> {

        });

        JScrollPane scrollPane = new JScrollPane(
            this.pane,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setUI(new FlatSmoothScrollPaneUI());

        this.setLayout(new BorderLayout());

        this.add(this.addressBar, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        this.loadFromFile(new File("src/main/resources/simple.gmi"));
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

    public void loadFromUrl(String url) {
        this.currentUri = URI.create(url);

        this.currentWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (GeminiResponse response = Tab.this.client.send(new GeminiRequest(url))) {
                    Tab.this.load(url, response.getInputStream());
                } catch (UnknownHostException e) {
                    SwingUtilities.invokeLater(() -> {
                        pane.clear();
                        pane.writeElement(new GemtextH1Element("Unknown host «" + currentUri.getHost() + "»"));
                        pane.writeElement(new GemtextParagraphElement("Host «" + currentUri.getHost() + "» does not exist. " +
                            "Check if the URL is correct."));
                    });
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        pane.clear();
                        pane.writeElement(new GemtextH1Element("Error loading " + url));
                        pane.writeElement(new GemtextParagraphElement(e.toString()));
                    });
                }

                return null;
            }
        };
        this.currentWorker.execute();
    }

    private void reload() {
        this.pane.clear();

        this.loadFromUrl(this.currentUri.toString());
    }
}
