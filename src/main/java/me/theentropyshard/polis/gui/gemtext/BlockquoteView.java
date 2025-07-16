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

import javax.swing.*;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import java.awt.*;

public class BlockquoteView extends ParagraphView {
    public BlockquoteView(Element element) {
        super(element);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        Rectangle b = a.getBounds();

        Graphics2D g2d = ((Graphics2D) g.create());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(UIManager.getColor("secondaryContainer"));
        g2d.fillRoundRect(b.x, b.y, b.width, b.height, 16, 16);

        g2d.setColor(UIManager.getColor("primary"));
        g2d.setFont(g2d.getFont().deriveFont(24f));
        g2d.drawString("❝", b.x + 4 + this.getLeftInset() / 3, b.y + g2d.getFont().getSize() + this.getTopInset());

        g2d.fillRoundRect(b.x, b.y, 16, b.height, 16, 16);
        g2d.setColor(UIManager.getColor("secondaryContainer"));
        g2d.fillRect(b.x + 4, b.y, 12, b.height);

        super.paint(g, a);
    }

    @Override
    protected short getLeftInset() {
        return 48;
    }

    @Override
    protected short getRightInset() {
        return 12;
    }

    @Override
    protected short getBottomInset() {
        return 16;
    }

    @Override
    protected short getTopInset() {
        return 16;
    }

    @Override
    public float getPreferredSpan(int axis) {
        if (axis == View.X_AXIS) {
            return super.getPreferredSpan(axis);
        } else if (axis == View.Y_AXIS) {
            return super.getPreferredSpan(axis);
        } else {
            throw new IllegalArgumentException("Unknown axis: " + axis);
        }
    }
}
