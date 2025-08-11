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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import me.theentropyshard.polis.History;
import me.theentropyshard.polis.gemini.client.GeminiClient;
import me.theentropyshard.polis.gemini.client.GeminiRequest;
import me.theentropyshard.polis.gemini.client.GeminiResponse;
import me.theentropyshard.polis.gemini.gemtext.GemtextParser;
import me.theentropyshard.polis.gemini.gemtext.document.GemtextH1Element;
import me.theentropyshard.polis.gemini.gemtext.document.GemtextParagraphElement;
import me.theentropyshard.polis.gui.addressbar.AddressBar;
import me.theentropyshard.polis.gui.gemtext.GemtextPane;
import me.theentropyshard.polis.utils.SwingUtils;

public class Tab extends JPanel {
    public static final boolean USE_PROXY = true;

    private final GeminiClient client;

    private AddressBar addressBar;
    private final GemtextPane gemtextPane;
    private final JScrollPane scrollPane;

    private final History history;

    private URI currentUri;
    private String hoveredUrl;

    private SwingWorker<Void, Void> currentWorker;

    private boolean uriFieldWasFocused;

    public Tab(GeminiClient client) {
        this.setLayout(new BorderLayout());

        this.client = client;

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem savePageItem = new JMenuItem("Save page");
        savePageItem.addActionListener(e -> {

        });
        popupMenu.add(savePageItem);

        this.gemtextPane = new GemtextPane();

        this.history = new History();

        this.addressBar = new AddressBar(input -> {
            URI uri;

            try {
                uri = new URI(input);
            } catch (URISyntaxException e) {
                this.handleException(e);

                return;
            }

            String scheme = uri.getScheme();
            if (scheme == null || scheme.trim().isEmpty()) {
                uri = URI.create("gemini://" + uri);
            }

            String path = uri.getPath();
            if (path == null || path.trim().isEmpty()) {
                uri = URI.create(uri + "/");
            }

            this.historyVisit(uri);

            this.load(uri);
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

        this.scrollPane = new JScrollPane(
            this.gemtextPane,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        this.scrollPane.setUI(new FlatSmoothScrollPaneUI());

        this.add(new JLayer<>(this.scrollPane, new LayerUI<>() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);

                String url = Tab.this.hoveredUrl;

                if (url == null) {
                    return;
                }

                Dimension size = c.getSize();
                Rectangle2D bounds = g.getFontMetrics().getStringBounds(url, g);

                int rectX = 0;
                int rectY = (int) (size.height - bounds.getHeight()) - 3;
                int rectWidth = (int) bounds.getWidth() + 6;
                int rectHeight = (int) bounds.getHeight() + 3;

                g.setColor(UIManager.getColor("linkPreviewBackground"));
                g.fillRect(rectX, rectY, rectWidth, rectHeight);

                g.setColor(UIManager.getColor("linkPreviewBorder"));
                g.drawRect(rectX, rectY, rectWidth, rectHeight);

                int textX = 3;
                int textY = size.height - g.getFontMetrics().getMaxDescent() - 3;

                g.drawString(url, textX, textY);
            }
        }), BorderLayout.CENTER);

        SwingUtils.createAction(
            this,
            "Ctrl+L",
            KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK),
            e -> this.addressBar.getUriField().requestFocus()
        );

        SwingUtils.createAction(
            this,
            "F5",
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            e -> this.refresh()
        );

        this.updateButtons();

        this.addressBar.getBackButton().addActionListener(e -> this.navigateBack());
        this.addressBar.getForwardButton().addActionListener(e -> this.navigateForward());
        this.addressBar.getRefreshButton().addActionListener(e -> this.refresh());

        this.gemtextPane.addHyperlinkListener(e -> {
            HyperlinkEvent.EventType type = e.getEventType();

            if (type == HyperlinkEvent.EventType.ACTIVATED) {
                URI uri = this.currentUri.resolve(e.getDescription());

                this.historyVisit(uri);
                this.load(uri);
            } else if (type == HyperlinkEvent.EventType.ENTERED) {
                this.hoveredUrl = e.getDescription();
            } else if (type == HyperlinkEvent.EventType.EXITED) {
                this.hoveredUrl = null;
            }

            this.repaint();
        });
    }

    public AddressBar getAddressBar() {
        return this.addressBar;
    }

    private void historyVisit(URI uri) {
        this.history.visit(uri);
        this.updateButtons();
    }

    private void navigateBack() {
        this.load(this.history.back());
        this.updateButtons();
    }

    private void navigateForward() {
        this.load(this.history.forward());
        this.updateButtons();
    }

    private void updateButtons() {
        this.addressBar.getBackButton().setEnabled(this.history.canNavigateBack());
        this.addressBar.getForwardButton().setEnabled(this.history.canNavigateForward());
    }

    public void readStream(InputStream inputStream) throws IOException {
        new GemtextParser().parse(inputStream, this.gemtextPane::writeElement);
    }

    public void refresh() {
        this.load(this.currentUri);
    }

    public void load(URI uri) {
        this.gemtextPane.clear();
        this.scrollPane.requestFocus();

        this.currentUri = uri;
        this.addressBar.setCurrentUri(this.currentUri.toString());

        if (this.currentWorker != null && !this.currentWorker.isDone()) {
            this.currentWorker.cancel(true);
        }

        this.currentWorker = SwingUtils.createWorker(() -> {
            try {
                String scheme = this.currentUri.getScheme();

                switch (scheme) {
                    case "gemini" -> {
                        if (Tab.USE_PROXY) {
                            this.loadFromUrlWithProxy(this.currentUri);
                        } else {
                            this.loadFromUrl(this.currentUri);
                        }
                    }

                    case "file" -> this.readStream(Files.newInputStream(Path.of(this.currentUri)));

                    default -> System.out.println("Unsupported scheme: " + scheme);
                }
            } catch (Exception e) {
                this.handleException(e);
            }
        });
        this.currentWorker.execute();
    }

    private void handleException(Exception e) {
        SwingUtilities.invokeLater(() -> {
            this.gemtextPane.clear();

            if (e instanceof UnknownHostException) {
                this.gemtextPane.writeElement(new GemtextH1Element("Unknown host «" + this.currentUri.getHost() + "»"));
                this.gemtextPane.writeElement(new GemtextParagraphElement("Host «" + this.currentUri.getHost() + "» does not exist. " +
                    "Check if the URL is correct."));
            } else {
                this.gemtextPane.writeElement(new GemtextH1Element("Error loading " + this.currentUri));
                this.gemtextPane.writeElement(new GemtextParagraphElement(e.toString()));
            }
        });
    }

    public void loadFromUrl(URI uri) throws Exception {
        try (GeminiResponse response = this.client.send(new GeminiRequest(uri))) {
            this.readStream(response.getInputStream());
        }
    }

    private void loadFromUrlWithProxy(URI uri) throws Exception {
        String theUrl = uri.getHost() + uri.getPath();

        String query = uri.getQuery();
        if (query != null && !query.trim().isEmpty()) {
            theUrl += URLEncoder.encode("?" + query, StandardCharsets.UTF_8);
        }

        this.readStream(new URL("https://portal.mozz.us/gemini/" + theUrl + "?raw=1").openConnection().getInputStream());
    }
}
