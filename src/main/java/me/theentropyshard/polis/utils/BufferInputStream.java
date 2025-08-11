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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An <code>InputStream</code> that "sniffs" on the given <code>InputStream</code> and saves bytes to an array.
 */
public class BufferInputStream extends InputStream {
    private final InputStream inputStream;

    private final ByteArrayOutputStream buffer;

    public BufferInputStream(InputStream inputStream) {
        this.inputStream = inputStream;

        this.buffer = new ByteArrayOutputStream(8192);
    }

    @Override
    public int read() throws IOException {
        int byteRead = this.inputStream.read();

        if (byteRead != -1) {
            this.buffer.write(byteRead);
        }

        return byteRead;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = this.inputStream.read(b, off, len);

        if (bytesRead != -1) {
            this.buffer.write(b, off, bytesRead);
        }

        return bytesRead;
    }

    public byte[] toByteArray() {
        return this.buffer.toByteArray();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
}