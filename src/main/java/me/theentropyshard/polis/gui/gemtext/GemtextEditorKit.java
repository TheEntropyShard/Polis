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

import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

public class GemtextEditorKit extends StyledEditorKit {
    private GemtextPane pane;

    public GemtextEditorKit(GemtextPane pane) {
        this.pane = pane;
    }

    @Override
    public Document createDefaultDocument() {
        return new GemtextDocument();
    }

    @Override
    public ViewFactory getViewFactory() {
        return new GemtextViewFactory(this.pane);
    }

    @Override
    public String getContentType() {
        return "text/gemini";
    }
}
