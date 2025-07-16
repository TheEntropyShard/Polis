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

import com.formdev.flatlaf.util.UIScale;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GemtextViewFactory implements ViewFactory {
    private final GemtextPane textPane;

    public GemtextViewFactory(GemtextPane gemtextPane) {
        this.textPane = gemtextPane;
    }

    @Override
    public View create(Element element) {
        String kind = element.getName();

        if (kind != null) {
            switch (kind) {
                case "blockquote" -> {
                    return new BlockquoteView(element);
                }
                case "listItem" -> {
                    return new ListItemView(element);
                }
                case "link" -> {
                    return new LinkView(element);
                }
                case AbstractDocument.ContentElementName -> {
                    return new LabelView(element);
                }
                case AbstractDocument.ParagraphElementName -> {
                    return new ParagraphView(element);
                }
                case AbstractDocument.SectionElementName -> {
                    return new BoxView(element, View.Y_AXIS) {
                        @Override
                        protected short getLeftInset() {
                            return 240;
                        }

                        @Override
                        protected short getRightInset() {
                            return 240;
                        }
                    };
                }
                case StyleConstants.ComponentElementName -> {
                    return new ComponentView(element);
                }
                case StyleConstants.IconElementName -> {
                    return new CustomIconView(textPane, element);
                }
            }
        }

        return new LabelView(element);
    }

    private static class CustomIconView extends IconView {

        private final GemtextPane textPane;

        public CustomIconView(GemtextPane textPane, Element elem) {
            super(elem);
            this.textPane = textPane;
        }

        @Override
        public float getAlignment(int axis) {
            if (axis == X_AXIS) {
                return super.getAlignment(axis);
            } else {
                //  Set Icon alignment to top
                return 0.8f;
            }
        }

        @Override
        public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
            int next = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
            if (direction == SwingConstants.WEST && (pos == -1 || pos > getStartOffset())) {
                //  Still have issues when press arrow key to move cursor back
                return getStartOffset();
            }
            if (direction == SwingConstants.EAST && pos != -1) {
                return getEndOffset();
            }
            return next;
        }

        private boolean isSelected() {
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            if (start == end) {
                return false;
            }
            if (start <= getStartOffset() && end >= getEndOffset()) {
                return true;
            }
            return false;
        }

        @Override
        public void paint(Graphics g, Shape a) {
            if (isSelected()) {
                //  For test not yet fix
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(textPane.getSelectionColor());
                Rectangle2D rectangle = a.getBounds2D();
                float y = UIScale.scale(0.8f);
                float h = UIScale.scale(1.4f);
                g2.fill(new Rectangle2D.Double(rectangle.getX(), rectangle.getY() + y, rectangle.getWidth(), rectangle.getHeight() - h));
                g2.dispose();
            }
            super.paint(g, a);
        }
    }
}
