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

package me.theentropyshard.polis.gui;

import java.awt.*;
import java.io.IOException;

import me.theentropyshard.polis.utils.ResourceUtils;

public class FontLoader {
    public static void loadFonts() {
        FontLoader.loadNotoSansMono();
    }

    private static void loadNotoSansMono() {
        String[] paths = {
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Regular.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Black.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Bold.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-ExtraBold.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-ExtraLight.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Light.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Medium.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-SemiBold.ttf",
            "/fonts/Noto_Sans_Mono/static/NotoSansMono-Thin.ttf"
        };

        FontLoader.loadFonts(paths);
    }

    private static void loadFonts(String[] paths) {
        for (String path : paths) {
            try {
                FontLoader.loadFont(path);
            } catch (IOException e) {
                System.err.println("Could not load font from: " + path);
                e.printStackTrace();
            } catch (FontFormatException e) {
                System.err.println("Invalid font file: " + path);
                e.printStackTrace();
            }
        }
    }

    private static void loadFont(String path) throws IOException, FontFormatException {
        Font font = Font.createFont(Font.TRUETYPE_FONT, ResourceUtils.openStream(path));
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if (!env.registerFont(font)) {
            System.err.println("Failed to register font: " + path);
        }
    }
}
