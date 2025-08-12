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

package me.theentropyshard.polis.gemini.client;

public enum StatusCode {
    // 10-19 Input expected
    INPUT_EXPECTED(10),
    SENSITIVE_INPUT_EXPECTED(11),

    // 20-29 Success
    SUCCESS(20),

    // 30-39 Redirection
    TEMPORARY_REDIRECTION(30),
    PERMANENT_REDIRECTION(31),

    // 40-49 Temporary failure
    TEMPORARY_FAILURE(40),
    SERVER_UNAVAILABLE(41),
    CGI_ERROR(42),
    PROXY_ERROR(43),
    SLOW_DOWN(44),

    // 50-59 Permanent failure
    PERMANENT_FAILURE(50),
    NOT_FOUND(51),
    GONE(52),
    PROXY_REQUEST_REFUSED(53),
    BAD_REQUEST(59),

    // 60-69 Client certificates
    CERTIFICATE_REQUIRED(60),
    CERTIFICATE_NOT_AUTHORIZED(61),
    CERTIFICATE_NOT_VALID(62),

    UNKNOWN(-1);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
