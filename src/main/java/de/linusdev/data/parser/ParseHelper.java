/*
 * Copyright (c) 2023 Linus Andera
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.linusdev.data.parser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ParseHelper {

    public static final String[] ESCAPE_PRE_ZEROS = {"", "0", "00", "000", "0000"};

    public static void escape2(@NotNull String s, @NotNull StringBuilder str) {
        try {
            escape2(s, (Appendable) str);
        } catch (IOException ignored) {
            //will never happen, because StringBuilder does not throw this Exception
        }
    }

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void escape2(@NotNull String s, @NotNull Appendable str) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if(c == '/') {
                str.append("\\/");

            } else if (c == '\"') {
                str.append("\\\"");

            } else if (c == '\\') {
                str.append("\\\\");

            } else if (c == '\n') {
                str.append("\\n");

            } else if (c == '\f') {
                str.append("\\f");

            } else if (c == '\r') {
                str.append("\\r");

            } else if (c == '\t') {
                str.append("\\t");

            } else if (c == '\b') {
                str.append("\\b");

            } else if (c < '\u0020' || (c >= '\u007F' && c <= '\u009F')){
                String ss = Integer.toHexString(c);
                str.append("\\u");
                str.append(ESCAPE_PRE_ZEROS[Math.max(0, 4 - ss.length())]);
                str.append(ss.toUpperCase());
            } else {
                str.append(c);

            }
        }
    }
}
