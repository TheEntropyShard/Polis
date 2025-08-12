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

import javax.swing.text.*;

public class GemtextViewFactory implements ViewFactory {
    private final GemtextPane textPane;

    public GemtextViewFactory(GemtextPane gemtextPane) {
        this.textPane = gemtextPane;
    }

    @Override
    public View create(Element element) {
        String kind = element.getName();

        if (kind == null) {
            return new LabelView(element);
        }

        return switch (kind) {
            case "blockquote" -> new BlockquoteView(element);
            case "listItem" -> new ListItemView(element);
            case "link" -> new LinkView(element);
            case AbstractDocument.ContentElementName -> new LabelView(element);
            case AbstractDocument.ParagraphElementName -> new ParagraphView(element);
            case AbstractDocument.SectionElementName -> new BoxView(element, View.Y_AXIS) {
                @Override
                protected short getLeftInset() {
                    return 240;
                }

                @Override
                protected short getRightInset() {
                    return 240;
                }
            };

            case StyleConstants.ComponentElementName -> new ComponentView(element);
            case StyleConstants.IconElementName -> new EmojiView(element, this.textPane);

            default -> new LabelView(element);
        };
    }
}
