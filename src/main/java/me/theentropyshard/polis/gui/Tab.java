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
import javax.swing.event.HyperlinkEvent;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
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
    public static final boolean USE_PROXY = true;

    private final GeminiClient client;

    private final AddressBar addressBar;
    private final GemtextPane gemtextPane;
    private final JScrollPane scrollPane;

    private URI currentUri;

    private SwingWorker<Void, Void> currentWorker;

    private String hoveredUrl;

    public Tab(GeminiClient client) {
        this.client = client;

        this.gemtextPane = new GemtextPane();
        this.gemtextPane.addHyperlinkListener(e -> {
            HyperlinkEvent.EventType type = e.getEventType();

            if (type == HyperlinkEvent.EventType.ACTIVATED) {
                this.currentUri = this.currentUri.resolve(e.getDescription());

                this.reload();
            } else if (type == HyperlinkEvent.EventType.ENTERED) {
                this.hoveredUrl = e.getDescription();
            } else if (type == HyperlinkEvent.EventType.EXITED) {
                this.hoveredUrl = null;
            }

            this.repaint();
        });

        this.scrollPane = new JScrollPane(
            this.gemtextPane,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.scrollPane.setUI(new FlatSmoothScrollPaneUI());

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem savePageItem = new JMenuItem("Save page");
        savePageItem.addActionListener(e -> {

        });
        popupMenu.add(savePageItem);

        this.addressBar = new AddressBar(input -> {
            if (!input.startsWith("gemini://")) {
                input = "gemini://" + input;
            }

            this.currentUri = URI.create(input);

            this.scrollPane.requestFocus();

            this.reload();
        });

        this.addressBar.getMoreButton().addActionListener(e -> {
            JButton b = (JButton) e.getSource();

            popupMenu.show(
                this,
                this.getParent().getPreferredSize().width - popupMenu.getPreferredSize().width,
                b.getY() + b.getPreferredSize().height
            );
        });

        this.add(this.addressBar, BorderLayout.NORTH);

        this.add(new JLayer<>(this.scrollPane, new LayerUI<>() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);

                String url = Tab.this.hoveredUrl;

                if (url == null) {
                    return;
                }

                Dimension size = c.getSize();
                int width = size.width;
                int height = size.height;

                Rectangle2D bounds = g.getFontMetrics().getStringBounds(url, g);

                int rectX = 0;
                int rectY = (int) (height - bounds.getHeight()) - 3;
                int rectWidth = (int) bounds.getWidth() + 6;
                int rectHeight = (int) bounds.getHeight() + 3;

                g.setColor(Color.decode("#EDF2FA"));
                g.fillRect(rectX, rectY, rectWidth, rectHeight);

                g.setColor(Color.BLACK);
                g.drawRect(rectX, rectY, rectWidth, rectHeight);

                int textX = 3;
                int textY = height - g.getFontMetrics().getMaxDescent() - 3;

                g.drawString(url, textX, textY);
            }
        }), BorderLayout.CENTER);

        this.getActionMap().put("ctrl_l", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tab.this.addressBar.requestFocus();
            }
        });

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK), "ctrl_l");

        this.setLayout(new BorderLayout());
    }

    public void load(String location, InputStream inputStream) throws IOException {
        this.addressBar.setCurrentUri(location);

        new GemtextParser().parse(inputStream, this.gemtextPane::writeElement);
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

        String path = this.currentUri.getPath();
        if (path == null || path.trim().isEmpty()) {
            this.currentUri = URI.create(this.currentUri.toString() + "/");
        }

        this.currentWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (Tab.USE_PROXY) {
                    String theUrl = Tab.this.currentUri.getHost() + Tab.this.currentUri.getPath();

                    String query = Tab.this.currentUri.getQuery();
                    if (query != null && !query.trim().isEmpty()) {
                        theUrl += URLEncoder.encode("?" + query, StandardCharsets.UTF_8);
                    }

                    HttpURLConnection c = (HttpURLConnection) new URL(
                        "https://portal.mozz.us/gemini/" + theUrl + "?raw=1"
                    ).openConnection();

                    Tab.this.load(url, c.getInputStream());
                } else {
                    try (GeminiResponse response = Tab.this.client.send(new GeminiRequest(url))) {
                        Tab.this.load(url, response.getInputStream());
                    } catch (UnknownHostException e) {
                        SwingUtilities.invokeLater(() -> {
                            Tab.this.gemtextPane.clear();
                            Tab.this.gemtextPane.writeElement(new GemtextH1Element("Unknown host «" + Tab.this.currentUri.getHost() + "»"));
                            Tab.this.gemtextPane.writeElement(new GemtextParagraphElement("Host «" + Tab.this.currentUri.getHost() + "» does not exist. " +
                                "Check if the URL is correct."));
                        });
                    } catch (Exception e) {
                        SwingUtilities.invokeLater(() -> {
                            Tab.this.gemtextPane.clear();
                            Tab.this.gemtextPane.writeElement(new GemtextH1Element("Error loading " + url));
                            Tab.this.gemtextPane.writeElement(new GemtextParagraphElement(e.toString()));
                        });
                    }
                }

                return null;
            }
        };
        this.currentWorker.execute();
    }

    private void reload() {
        this.gemtextPane.clear();

        this.loadFromUrl(this.currentUri.toString());
    }
}
