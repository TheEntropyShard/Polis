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

import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import java.awt.*;

public class ListItemView extends ParagraphView {
    public ListItemView(Element element) {
        super(element);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        Rectangle b = a.getBounds();

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        super.paint(g, a);

        // TODO: choose color based on theme
        g2d.setColor(Color.BLACK);

        int size = 4;

        g2d.fillOval(b.x + 16, b.y + (g.getFont().getSize() + size) / 2, size, size);
    }

    @Override
    protected short getLeftInset() {
        return 32;
    }
}
