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

package me.theentropyshard.polis.gui.emoji;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.theentropyshard.polis.utils.ResourceUtils;

public class EmojiSupport {
    private static BufferedImage sheet;
    private static final Map<String, JsonObject> emojis = new HashMap<>();
    private static final Pattern emojiPattern = Pattern.compile("&#x(\\w+);");

    public static void init() {
        try {
            EmojiSupport.sheet = ImageIO.read(ResourceUtils.openStream("/emoji/sheet_google_64.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonArray jsonArray = new Gson()
            .fromJson(
                new InputStreamReader(
                    ResourceUtils.openStream("/emoji/emoji.json"), StandardCharsets.UTF_8), JsonArray.class);

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            JsonElement nonQualified = obj.get("non_qualified");

            if (!nonQualified.isJsonNull()) {
                EmojiSupport.emojis.put(nonQualified.getAsString(), obj);
            }

            EmojiSupport.emojis.put(obj.get("unified").getAsString(), obj);
        }
    }

    public static Image getEmoji(String code) {
        Matcher matcher = EmojiSupport.emojiPattern.matcher(code);
        List<String> s = new ArrayList<>();
        while (matcher.find()) {
            String m = matcher.group(1);
            int length = m.length();
            if (length <= 4) {
                s.add("0000".substring(length) + m);
            } else {
                s.add(m);
            }
        }

        JsonObject obj = EmojiSupport.emojis.get(String.join("-", s));

        if (obj != null) {
            int sheetX = obj.get("sheet_x").getAsInt();
            int sheetY = obj.get("sheet_y").getAsInt();
            int x = (sheetX * (64 + 2)) + 1;
            int y = (sheetY * (64 + 2)) + 1;

            return EmojiSupport.sheet
                .getSubimage(x + 1, y + 1, 64, 64)
                .getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH);
        } else {
            return null;
        }
    }
}
