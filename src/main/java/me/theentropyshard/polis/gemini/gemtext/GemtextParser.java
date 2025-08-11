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

package me.theentropyshard.polis.gemini.gemtext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import me.theentropyshard.polis.gemini.gemtext.document.*;
import me.theentropyshard.polis.utils.BufferInputStream;

public class GemtextParser {
    private static final String PREFORMATTED_START = "```";
    private static final String LIST_ITEM_START = "* ";
    private static final String H1_START = "# ";
    private static final String H2_START = "## ";
    private static final String H3_START = "### ";
    private static final String BLOCKQUOTE_START = "> ";
    private static final String LINK_START = "=>";

    public GemtextParser() {

    }

    public byte[] parse(InputStream inputStream, Consumer<GemtextElement> elementConsumer) throws IOException {
        BufferInputStream bufferInputStream = new BufferInputStream(inputStream);

        boolean inPreBlock = false;
        String preCaption = null;
        List<String> preContents = new ArrayList<>();
        String title = null;

        GemtextListElement listElement = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(bufferInputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (Thread.interrupted()) {
                    return new byte[0];
                }

                if (!line.startsWith(GemtextParser.LIST_ITEM_START) && listElement != null) {
                    elementConsumer.accept(listElement);
                    listElement = null;
                }

                if (line.startsWith(GemtextParser.PREFORMATTED_START)) {
                    if (inPreBlock) {
                        elementConsumer.accept(new GemtextPreformattedElement(preCaption, String.join("\n", preContents)));
                        preContents = new ArrayList<>();
                    } else {
                        String caption = line.substring(3);
                        preCaption = caption.isEmpty() ? null : caption;
                    }

                    inPreBlock = !inPreBlock;
                } else if (inPreBlock) {
                    preContents.add(line);
                } else if (line.startsWith(GemtextParser.LIST_ITEM_START)) {
                    if (listElement == null) {
                        listElement = new GemtextListElement();
                    }

                    listElement.add(line.substring(2));
                } else if (line.startsWith(GemtextParser.H1_START)) {
                    String h1Content = line.substring(2);

                    if (title == null) {
                        title = h1Content;
                    }

                    elementConsumer.accept(new GemtextH1Element(h1Content));
                } else if (line.startsWith(GemtextParser.H2_START)) {
                    elementConsumer.accept(new GemtextH2Element(line.substring(3)));
                } else if (line.startsWith(GemtextParser.H3_START)) {
                    elementConsumer.accept(new GemtextH3Element(line.substring(4)));
                } else if (line.startsWith(GemtextParser.BLOCKQUOTE_START)) {
                    elementConsumer.accept(new GemtextBlockquoteElement(line.substring(2)));
                } else if (line.startsWith(GemtextParser.LINK_START)) {
                    line = line.substring(2).trim();

                    int spaceIndex = -1;

                    for (char c : line.toCharArray()) {
                        if (Character.isWhitespace(c)) {
                            spaceIndex = line.indexOf(c);

                            break;
                        }
                    }

                    String link;
                    String label;

                    if (spaceIndex == -1) {
                        link = line;
                        label = null;
                    } else {
                        link = line.substring(0, spaceIndex);
                        label = line.substring(spaceIndex).trim();
                    }

                    elementConsumer.accept(new GemtextLinkElement(link, label));
                } else {
                    elementConsumer.accept(new GemtextParagraphElement(line));
                }
            }
        }

        if (listElement != null) {
            elementConsumer.accept(listElement);
        }

        return bufferInputStream.toByteArray();
    }
}
