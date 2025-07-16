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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.Position;
import java.awt.*;

public class LinkView extends LabelView {
    private Rectangle b;

    public LinkView(Element element) {
        super(element);
    }

    @Override
    public Shape modelToView(int pos, Shape a, Position.Bias bias) throws BadLocationException {
        return this.b == null ? super.modelToView(pos, a, bias) : this.b;
    }

    @Override
    public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1, Shape a) throws BadLocationException {
        return this.b == null ? super.modelToView(p0, b0, p1, b1, a) : this.b;
    }

    @Override
    public void paint(Graphics g, Shape a) {
        this.b = a.getBounds();

        super.paint(g, a);
    }
}
