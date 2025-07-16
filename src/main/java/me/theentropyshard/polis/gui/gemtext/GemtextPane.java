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

import net.fellbaum.jemoji.Emoji;
import net.fellbaum.jemoji.EmojiManager;
import net.fellbaum.jemoji.IndexedEmoji;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import me.theentropyshard.polis.gemini.gemtext.document.*;
import me.theentropyshard.polis.gui.emoji.EmojiSupport;
import me.theentropyshard.polis.utils.ListUtils;
import me.theentropyshard.polis.utils.StringUtils;

public class GemtextPane extends JTextPane {
    private static final String EMOJI_RANDOM_KEY = StringUtils.generateRandomString(10);

    private final Map<String, String> currentEmojis;

    public GemtextPane(Consumer<String> uriConsumer) {
        this.currentEmojis = new HashMap<>();

        Action myCopy = new AbstractAction("copy") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = ((JTextPane) e.getSource()).getSelectedText();

                for (Map.Entry<String, String> entry : GemtextPane.this.currentEmojis.entrySet()) {
                    selected = selected.replace(entry.getKey(), entry.getValue());
                }

                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                c.setContents(new StringSelection(selected), null);
            }
        };

        this.getActionMap().put(DefaultEditorKit.copyAction, myCopy);
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), DefaultEditorKit.copyAction);

        this.setEditorKit(new GemtextEditorKit(this));
        this.setEditable(false);

        this.addHyperlinkListener(e -> {
            HyperlinkEvent.EventType type = e.getEventType();

            if (type == HyperlinkEvent.EventType.ACTIVATED) {
                uriConsumer.accept(e.getDescription());
            } else {
                GemtextDocument document = (GemtextDocument) this.getDocument();
                Element element = e.getSourceElement();
                int startOffset = element.getStartOffset();
                int endOffset = element.getEndOffset();

                MutableAttributeSet attrs = new SimpleAttributeSet();

                if (type == HyperlinkEvent.EventType.ENTERED) {
                    StyleConstants.setForeground(attrs, Color.BLUE);
                } else if (type == HyperlinkEvent.EventType.EXITED) {
                    StyleConstants.setForeground(attrs, UIManager.getColor("primary"));
                } else {
                    throw new RuntimeException("Unknown event: " + type);
                }

                document.setCharacterAttributes(
                    startOffset, endOffset - startOffset, attrs, false
                );
            }
        });

        LinkController controller = new LinkController(this);

        this.addMouseListener(controller);
        this.addMouseMotionListener(controller);
    }

    public void clear() {
        this.setText("");
    }

    private void writeLine(String text, MutableAttributeSet characterAttrs, MutableAttributeSet paragraphAttrs) {
        text = text + "\n";

        List<IndexedEmoji> indexedEmojis = EmojiManager.extractEmojisInOrderWithIndex(text);

        if (indexedEmojis.isEmpty()) {
            this.insertString(text, characterAttrs, paragraphAttrs);

            return;
        }

        StringBuilder accumulator = new StringBuilder();

        int i = 0;

        while (i < text.length()) {
            int a = i;

            IndexedEmoji indexedEmoji = ListUtils.search(indexedEmojis, e -> e.getCharIndex() == a);

            if (indexedEmoji != null) {
                i += indexedEmoji.getEndCharIndex() - indexedEmoji.getCharIndex() - 1;

                if (!accumulator.isEmpty()) {
                    this.insertString(accumulator.toString(), characterAttrs, paragraphAttrs);
                    accumulator.setLength(0);
                }

                Emoji emoji = indexedEmoji.getEmoji();
                Image texture = EmojiSupport.getEmoji(emoji.getHtmlHexadecimalCode());

                if (texture != null) {
                    this.insertEmoji(texture, emoji);
                }
            } else {
                accumulator.append(text.charAt(i));
            }

            i++;
        }

        if (!accumulator.isEmpty()) {
            this.insertString(accumulator.toString(), characterAttrs, paragraphAttrs);
            accumulator.setLength(0);
        }
    }

    private void insertString(String s, MutableAttributeSet attrs, MutableAttributeSet paragraphAttrs) {
        GemtextDocument document = (GemtextDocument) this.getDocument();
        int length = document.getLength();

        try {
            document.insertString(length, s, attrs);
            document.setParagraphAttributes(length, s.length(), paragraphAttrs, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void insertEmoji(Image image, Emoji emoji) {
        MutableAttributeSet mas = new SimpleAttributeSet();
        StyleConstants.setIcon(mas, new ImageIcon(image));
        String emojiKey = GemtextPane.EMOJI_RANDOM_KEY + ":" + emoji.getHtmlHexadecimalCode();
        this.currentEmojis.put(emojiKey, emoji.getEmoji());
        this.insertString(emojiKey, mas, new SimpleAttributeSet());
    }

    private void writeComponent(JComponent component, MutableAttributeSet attrs) {
        StyleConstants.setComponent(attrs, component);
        this.writeLine(" ", attrs, attrs);
        attrs.removeAttribute(StyleConstants.ComponentElementName);
        attrs.removeAttribute(AbstractDocument.ElementNameAttribute);
    }

    private void setHeaderStyle(MutableAttributeSet attrs, int size) {
        StyleConstants.setFontFamily(attrs, "Noto Sans Mono Bold");
        StyleConstants.setFontSize(attrs, size);
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_CENTER);
    }

    private void setParagraphStyle(MutableAttributeSet attrs) {
        StyleConstants.setFontFamily(attrs, "Noto Sans Mono");
        StyleConstants.setFontSize(attrs, 16);
        StyleConstants.setAlignment(attrs, StyleConstants.ALIGN_JUSTIFIED);
    }

    public void writeElement(GemtextElement element) {
        if (element instanceof GemtextBlockquoteElement blockquote) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setParagraphStyle(attrs);

            attrs.addAttribute(AbstractDocument.ElementNameAttribute, "blockquote");
            this.writeLine(blockquote.getText(), attrs, attrs);
            attrs.removeAttribute(AbstractDocument.ElementNameAttribute);
        } else if (element instanceof GemtextH1Element h1) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setHeaderStyle(attrs, 24);

            this.writeLine(h1.getText(), attrs, attrs);
        } else if (element instanceof GemtextH2Element h2) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setHeaderStyle(attrs, 22);

            this.writeLine(h2.getText(), attrs, attrs);
        } else if (element instanceof GemtextH3Element h3) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setHeaderStyle(attrs, 20);

            this.writeLine(h3.getText(), attrs, attrs);
        } else if (element instanceof GemtextLinkElement link) {
            MutableAttributeSet characterAttrs = new SimpleAttributeSet();
            MutableAttributeSet paragraphAttrs = new SimpleAttributeSet();

            this.setParagraphStyle(paragraphAttrs);

            StyleConstants.setUnderline(characterAttrs, true);
            StyleConstants.setForeground(characterAttrs, UIManager.getColor("primary"));

            String uri = link.getLink();

            characterAttrs.addAttribute("uri", uri);
            characterAttrs.addAttribute(AbstractDocument.ElementNameAttribute, "link");

            String label = link.getLabel();
            this.writeLine(link.hasLabel() ? label : uri, characterAttrs, paragraphAttrs);
        } else if (element instanceof GemtextListElement list) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setParagraphStyle(attrs);

            attrs.addAttribute(AbstractDocument.ElementNameAttribute, "listItem");
            for (String line : list) {
                this.writeLine(line, attrs, attrs);
            }
            attrs.removeAttribute(AbstractDocument.ElementNameAttribute);
        } else if (element instanceof GemtextParagraphElement paragraph) {
            MutableAttributeSet attrs = new SimpleAttributeSet();

            this.setParagraphStyle(attrs);

            this.writeLine(paragraph.getText(), attrs, attrs);
        } else if (element instanceof GemtextPreformattedElement preformatted) {
            RSyntaxTextArea textArea = new RSyntaxTextArea();
            textArea.setEditable(false);
            textArea.setCurrentLineHighlightColor(new Color(0, 0, 0, 0));
            textArea.setText(preformatted.getText());

            MutableAttributeSet attrs = new SimpleAttributeSet();
            this.writeComponent(textArea, attrs);
        }
    }
}
