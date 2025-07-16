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

package me.theentropyshard.polis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ResourceUtils {
    public static InputStream openStream(String path) {
        return ResourceUtils.class.getResourceAsStream(path);
    }

    public static byte[] readBytes(String path) {
        try (InputStream inputStream = ResourceUtils.openStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource " + path);
            }

            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource " + path, e);
        }
    }

    public static String readString(String path) {
        return new String(ResourceUtils.readBytes(path), StandardCharsets.UTF_8);
    }

    private ResourceUtils() {
        throw new UnsupportedOperationException();
    }
}
