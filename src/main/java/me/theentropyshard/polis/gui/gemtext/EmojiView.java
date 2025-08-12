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

public class EmojiView extends IconView {
    private final GemtextPane textPane;

    public EmojiView(Element elem, GemtextPane textPane) {
        super(elem);

        this.textPane = textPane;
    }

    @Override
    public float getAlignment(int axis) {
        if (axis == View.X_AXIS) {
            return super.getAlignment(axis);
        } else {
            return 0.8f;
        }
    }

    @Override
    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
        int next = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);

        if (direction == SwingConstants.WEST && (pos == -1 || pos > this.getStartOffset())) {
            return this.getStartOffset();
        }

        if (direction == SwingConstants.EAST && pos != -1) {
            return this.getEndOffset();
        }

        return next;
    }

    private boolean isSelected() {
        int start = this.textPane.getSelectionStart();
        int end = this.textPane.getSelectionEnd();

        if (start == end) {
            return false;
        }

        return start <= this.getStartOffset() && end >= this.getEndOffset();
    }

    @Override
    public void paint(Graphics g, Shape a) {
        if (this.isSelected()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(this.textPane.getSelectionColor());
            Rectangle2D rectangle = a.getBounds2D();
            float y = UIScale.scale(0.8f);
            float h = UIScale.scale(1.4f);
            g2.fill(new Rectangle2D.Double(rectangle.getX(), rectangle.getY() + y, rectangle.getWidth(), rectangle.getHeight() - h));
            g2.dispose();
        }

        super.paint(g, a);
    }
}
