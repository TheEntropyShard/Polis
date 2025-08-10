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

package me.theentropyshard.polis;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;

public class History {
    private final Deque<URI> backStack;
    private final Deque<URI> forwardStack;

    private URI currentUri;

    public History() {
        this.backStack = new ArrayDeque<>();
        this.forwardStack = new ArrayDeque<>();
    }

    public void visit(URI uri) {
        if (this.currentUri != null) {
            this.backStack.push(this.currentUri);
        }

        this.currentUri = uri;
        this.forwardStack.clear();
    }

    public URI back() {
        if (this.canNavigateBack()) {
            this.forwardStack.push(this.currentUri);
            this.currentUri = this.backStack.pop();
        }

        return this.currentUri;
    }

    public URI forward() {
        if (this.canNavigateForward()) {
            this.backStack.push(this.currentUri);
            this.currentUri = this.forwardStack.pop();
        }

        return this.currentUri;
    }

    public boolean canNavigateBack() {
        return this.backStack.size() > 0;
    }

    public boolean canNavigateForward() {
        return this.forwardStack.size() > 0;
    }

    @Override
    public String toString() {
        return "History {\n\tBack Stack: " + this.backStack + "\n\tFS: " + this.forwardStack + "\n}";
    }
}
