/*
 * Copyright (c) 2022 Linus Andera
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

package me.linusdev.data.parser;

import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.parser.exceptions.ParseValueException;
import me.linusdev.data.parser.exceptions.UnexpectedCharacterException;
import me.linusdev.data.parser.exceptions.UnexpectedEndException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.Locale;

import static me.linusdev.data.parser.JsonParser.*;

/**
 * A {@link Reader} like class, that makes reading a json-object much easier.
 */
public class JsonReader {

    private final Reader reader;
    private int pushBack = -1;

    public JsonReader(Reader reader) {
        this.reader = reader;
    }

    /**
     * Pushes given character back, so the next {@link #read(ParseTracker)} call will read it.<br>
     * <b>The push back buffer supports only a single character, if the buffer is not empty, it will be overwritten,
     * and the old character will be lost.</b>
     * @param i character to push back
     */
    public void pushBack(int i) {
        this.pushBack = i;
    }

    /**
     * reads to the next character, which integer value is not below 33.
     *
     * @return next character or -1 if end of the stream is reached.
     */
    public int read(@NotNull ParseTracker tracker) throws IOException {
        int i;
        if (pushBack != -1) {
            i = pushBack;
            pushBack = -1;
            return i;
        }

        while ((i = reader.read()) != -1) {
            if (i == NEW_LINE_CHAR) {
                tracker.nextLine();
                continue;
            }
            //takes care of all control characters and \n, \t, space, ...
            if (i <= 32) continue;
            return i;
        }

        return -1;
    }

    /**
     * reads until ',' is read. the ',' will be pushed back.
     * @param tracker {@link ParseTracker}
     * @return read value, may be {@link Boolean}, {@code null} or {@link Number}
     * @throws IOException while reading or parsing
     * @throws ParseException while reading or parsing
     */
    public @Nullable Object readValue(@NotNull ParseTracker tracker) throws IOException, ParseException {
        StringBuilder str = new StringBuilder(50);

        int i = read(tracker);

        while (i != -1) {
            if (i == COMMA_CHAR || i == CURLY_BRACKET_CLOSE_CHAR || i == SQUARE_BRACKET_CLOSE_CHAR) {
                pushBack(i);
                String valueString = str.toString();

                if(valueString.isEmpty()) throw new UnexpectedCharacterException((char) i, tracker);

                if (valueString.equalsIgnoreCase(TRUE)) return true;
                else if (valueString.equalsIgnoreCase(FALSE)) return false;
                else if (valueString.equalsIgnoreCase(NULL)) return null;

                //it should be a number
                try {
                    return NumberFormat.getNumberInstance(Locale.ENGLISH).parse(valueString);
                } catch (java.text.ParseException e) {
                    throw new ParseValueException(e, valueString, tracker);
                }
            }

            str.append((char) i);

            i = read(tracker);
        }

        throw new UnexpectedEndException(tracker);
    }

    /**
     * reads until a '"' is read.
     * @param allowNewLine whether to allow a line break while reading
     * @param tracker {@link ParseTracker}
     * @return read {@link String}
     * @throws IOException while reading or parsing
     * @throws UnexpectedEndException while reading or parsing
     * @throws UnexpectedCharacterException while reading or parsing
     */
    public String readString(boolean allowNewLine, @NotNull ParseTracker tracker) throws IOException, UnexpectedEndException, UnexpectedCharacterException {
        StringBuilder str = new StringBuilder(1024);

        int i;
        char c;
        boolean escaped = false;
        char[] chars = new char[4];

        while ((i = reader.read()) != -1) {
            c = (char) i;

            if (escaped) {
                escaped = false;

                if (c == 'n') {
                    str.append('\n');

                } else if (c == 'f') {
                    str.append('\f');

                } else if (c == 'r') {
                    str.append('\r');

                } else if (c == 't') {
                    str.append('\t');

                } else if (c == 'b') {
                    str.append('\b');

                } else if (c == '\\') {
                    str.append('\\');

                } else if (c == '\"') {
                    str.append('\"');

                } else if (c == 'u') {
                    if (reader.read(chars) != 4) throw new UnexpectedEndException(tracker);
                    str.append((char) Integer.parseInt(new String(chars), 16));

                } else {
                    // sometimes '/' is escaped, as defined by the json language
                    str.append(c);

                }

            } else {

                if (c == '\\') {
                    escaped = true;

                } else if (c == '"') {
                    return str.toString();

                } else if (c == '\n') {
                    if (!allowNewLine) throw new UnexpectedCharacterException(c, tracker);
                    str.append(c);
                    tracker.nextLine();

                } else {
                    str.append(c);

                }
            }

        }

        throw new UnexpectedEndException(tracker);
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException cannotClose) {
            System.err.println("Cannot close reader!");
            cannotClose.printStackTrace();
        }
    }

}
