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

package me.theentropyshard.polis.gui.gemtext;

import javax.swing.event.HyperlinkEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class LinkController extends MouseAdapter {
    private final GemtextPane pane;
    private final GemtextDocument document;

    private boolean inLink;
    private String uri;
    private Element currentElement;

    public LinkController(GemtextPane pane) {
        this.pane = pane;
        this.document = (GemtextDocument) pane.getDocument();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            int position = this.pane.viewToModel2D(e.getPoint());
            Element element = this.document.getCharacterElement(position);
            AttributeSet attrs = element.getAttributes();
            Object uriAttr = attrs.getAttribute("uri");
            Rectangle2D r = this.pane.modelToView2D(position);

            if (uriAttr != null && r.contains(e.getPoint())) {
                this.currentElement = element;

                this.pane.fireHyperlinkUpdate(new HyperlinkEvent(
                    e.getSource(), HyperlinkEvent.EventType.ACTIVATED, null, String.valueOf(uriAttr), this.currentElement, e
                ));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        try {
            int p = this.pane.viewToModel2D(e.getPoint());
            Element element = this.document.getCharacterElement(p);
            AttributeSet attrs = element.getAttributes();
            Object uriAttr = attrs.getAttribute("uri");
            Rectangle2D r = this.pane.modelToView2D(p);

            if (r.contains(e.getPoint())) {
                if (uriAttr != null) {
                    String desc = String.valueOf(uriAttr);

                    if (this.uri != null && !this.uri.equals(desc)) {
                        this.linkExit(e, this.uri, this.currentElement);
                    }

                    if (!this.inLink) {
                        this.currentElement = element;
                        this.linkEnter(e, desc, this.currentElement);
                    }
                }
            } else {
                if (this.inLink) {
                    this.linkExit(e, this.uri, this.currentElement);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void linkEnter(MouseEvent event, String uri, Element element) {
        this.inLink = true;
        this.uri = uri;

        this.pane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.pane.fireHyperlinkUpdate(new HyperlinkEvent(
            event.getSource(), HyperlinkEvent.EventType.ENTERED, null, uri, element, event
        ));
    }

    private void linkExit(MouseEvent event, String uri, Element element) {
        this.inLink = false;

        this.pane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.pane.fireHyperlinkUpdate(new HyperlinkEvent(
            event.getSource(), HyperlinkEvent.EventType.EXITED, null, uri, element, event
        ));
    }
}
