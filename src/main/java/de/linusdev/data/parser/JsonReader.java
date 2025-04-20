/*
 * Copyright (c) 2023-2025 Linus Andera
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

import de.linusdev.data.parser.exceptions.ParseValueException;
import de.linusdev.lutils.other.parser.ParseException;
import de.linusdev.lutils.other.parser.ParseTracker;
import de.linusdev.lutils.other.parser.UnexpectedEndException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.text.NumberFormat;
import java.util.Locale;

import static de.linusdev.data.parser.JsonParser.*;

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
            if (i == JsonParser.NEW_LINE_CHAR) {
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
     * Reads the next char without skipping anything
     * @param tracker to track lines
     * @return the next char or -1 if the end has been reached.
     * @throws IOException while reading
     */
    public int readNextChar(@NotNull ParseTracker tracker) throws IOException {
        int i;
        if (pushBack != -1) {
            i = pushBack;
            pushBack = -1;
            return i;
        }

        i = reader.read();

        if(i == NEW_LINE_CHAR)
            tracker.nextLine();

        return i;
    }

    /**
     * Reads until EOL or EOF.
     * @param tracker {@link ParseTracker}
     * @return read value, may be {@link Boolean}, {@code null} or {@link Number}
     * @throws IOException while reading or parsing
     */
    public @NotNull String readToEOL(@NotNull ParseTracker tracker) throws IOException {
        StringBuilder str = new StringBuilder(50);

        int i = reader.read();

        while (i != -1) {
            if (i == NEW_LINE_CHAR) {
                tracker.nextLine();
                return str.toString();
            }

            str.append((char) i);
            i = reader.read();
        }

        if(str.charAt(str.length()-1) == '\r')
            str.setLength(str.length()-1);

        return str.toString();
    }

    public @NotNull String readMultiLineComment(@NotNull ParseTracker tracker) throws IOException, UnexpectedEndException {
        StringBuilder str = new StringBuilder(50);

        int i = reader.read();
        boolean asterisk = false;

        while (i != -1) {
            if(asterisk && i == SLASH_CHAR) {
                str.setLength(str.length()-1);
                return str.toString();
            }

            asterisk = i == ASTERISK_CHAR;

            if (i == NEW_LINE_CHAR)
                tracker.nextLine();

            str.append((char) i);
            i = reader.read();
        }

        throw new UnexpectedEndException(tracker);
    }

    /**
     * reads until {@value JsonParser#COMMA_CHAR}, {@value JsonParser#CURLY_BRACKET_CLOSE_CHAR},
     * {@value JsonParser#SQUARE_BRACKET_CLOSE_CHAR}, {@value JsonParser#SLASH_CHAR} is read.
     * the last char will be pushed back.
     * @param tracker {@link ParseTracker}
     * @return read value, may be {@link Boolean}, {@code null} or {@link Number}
     * @param allowToken whether {@link JsonParser#setIdentifyNumberValues(boolean)} is set to {@code true}.
     * @throws IOException while reading or parsing
     * @throws ParseException while reading or parsing
     */
    public @Nullable Object readValue(@NotNull ParseTracker tracker, boolean allowToken) throws IOException, ParseException {
        StringBuilder str = new StringBuilder(50);

        int i = read(tracker);

        while (i != -1) {
            if (i == COMMA_CHAR || i == CURLY_BRACKET_CLOSE_CHAR || i == SQUARE_BRACKET_CLOSE_CHAR || i == SLASH_CHAR) {
                pushBack(i);
                String valueString = str.toString();

                if(valueString.isEmpty()) throw new ParseException(tracker, (char) i);

                if (valueString.equalsIgnoreCase(JsonParser.TRUE)) return true;
                else if (valueString.equalsIgnoreCase(JsonParser.FALSE)) return false;
                else if (valueString.equalsIgnoreCase(JsonParser.NULL)) return null;

                //it should be a number
                if(allowToken){
                    try {
                        return switch (valueString.charAt(valueString.length() - 1)) {
                            case BYTE_TOKEN     ->  Byte.parseByte(valueString.substring(0, valueString.length() - 1));
                            case SHORT_TOKEN    ->  Short.parseShort(valueString.substring(0, valueString.length() - 1));
                            case INTEGER_TOKEN  ->  Integer.parseInt(valueString.substring(0, valueString.length() - 1));
                            case LONG_TOKEN     ->  Long.parseLong(valueString.substring(0, valueString.length() - 1));
                            case FLOAT_TOKEN    ->  Float.parseFloat(valueString.substring(0, valueString.length() - 1));
                            case DOUBLE_TOKEN   ->  Double.parseDouble(valueString.substring(0, valueString.length() - 1));
                            default             ->  NumberFormat.getNumberInstance(Locale.ENGLISH).parse(valueString);
                        };
                    } catch (NumberFormatException | java.text.ParseException e) {
                        throw new ParseValueException(e, valueString, tracker);
                    }
                } else {
                    try {
                        return NumberFormat.getNumberInstance(Locale.ENGLISH).parse(valueString);
                    } catch (java.text.ParseException e) {
                        throw new ParseValueException(e, valueString, tracker);
                    }
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
     * @throws ParseException while reading or parsing
     */
    public String readString(boolean allowNewLine, @NotNull ParseTracker tracker) throws IOException, ParseException {
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
                    if (!allowNewLine) throw new ParseException(tracker, c);
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
