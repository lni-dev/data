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

import de.linusdev.data.AbstractData;
import de.linusdev.data.Datable;
import de.linusdev.data.SimpleDatable;
import de.linusdev.data.entry.Entry;
import de.linusdev.data.implemantations.SODataListImpl;
import de.linusdev.data.parser.exceptions.ParseException;
import de.linusdev.data.parser.exceptions.ParseValueException;
import de.linusdev.data.parser.exceptions.UnexpectedCharacterException;
import de.linusdev.data.parser.exceptions.UnexpectedEndException;
import de.linusdev.data.so.SAOEntryImpl;
import de.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;


/**
 *
 * Replaced by {@link JsonParser}<br><br>
 *
 * This class is used to parse {@link AbstractData} to a json string and vice versa
 *
 * <br><br>
 *
 * <a style="margin-bottom:0; padding-bottom:0; font-size:10px">{@link AbstractData} to json-string can parse:</a>
 * <ul>
 *     <li>
 *         {@link Boolean}, {@link Byte}, {@link Short}, {@link Integer}, {@link Long}, {@link Float}, {@link Double}, {@link String},
 *     </li>
 *     <li>
 *         {@link Datable}
 *     </li>
 *     <li>
 *          any primitive type array
 *     </li>
 *     <li>
 *         {@link Object}[] and {@link Collection} of the before mentioned Classes
 *     </li>
 * </ul>
 * <br>
 * <a style="margin-bottom:0; padding-bottom:0; font-size:10px">json-string to {@link SOData SOData} can parse:</a>
 * <ul>
 *     <li style="padding-top:0">
 *         false/true to {@link Boolean} (ignores case)
 *     </li>
 *     <li>
 *         null to {@code null} (ignores case)
 *     </li>
 *     <li>
 *         Integer Numbers (1, 4, 5, ...) to {@link Long} <br>
 *     (if {@link #setIdentifyNumberValues(boolean)} is set to false (standard) while converting to json-string(!))
 *     </li>
 *     <li>
 *         Decimal Numbers (5.6, ...) to {@link Double} <br>
 *     (if {@link #setIdentifyNumberValues(boolean)} is set to false (standard) while converting to json-string(!))<br>
 *     </li>
 *     <li>
 *         "strings" to {@link String}
 *     </li>
 *     <li>
 *         Arrays ([...]) to {@link ArrayList<Object>}&lt;Object&gt;
 *     </li>
 *     <li>
 *         any other values are not supported and will most likely cause a {@link ParseException}
 *     </li>
 * </ul>
 */
@Deprecated()
public class OldJsonParser {

    private static final char BYTE_TOKEN = 'B';
    private static final char SHORT_TOKEN = 'S';
    private static final char INTEGER_TOKEN = 'I';
    private static final char LONG_TOKEN = 'L';
    private static final char FLOAT_TOKEN = 'F';
    private static final char DOUBLE_TOKEN = 'D';

    //readFromStream
    private Reader reader = null;
    private ParseTracker tracker = null;

    //toJsonString
    private StringBuilder str = null;

    //writeData
    private Writer writer;

    private SpaceOffsetTracker offset = null;

    private String offsetString = "  ";
    private boolean identifyNumberValues = false;

    public OldJsonParser() {

    }

    public SODataListImpl readDataFromResourceFile(String resource) throws IOException, ParseException, NullPointerException {
        SODataListImpl data;
        try {
            InputStream in = OldJsonParser.class.getClassLoader().getResourceAsStream(resource);
            if (in == null) return null;
            reader = new BufferedReader(new InputStreamReader(in));
            tracker = new ParseTracker();
            data = readDataFromStream(nextFromStream(true));
        } finally {
            reader.close();
        }

        return data;
    }

    public SODataListImpl readDataFromFile(Path filePath) throws IOException, ParseException {
        SODataListImpl data;
        try {
            reader = Files.newBufferedReader(filePath);
            tracker = new ParseTracker();
            data = readDataFromStream(nextFromStream(true));
        } finally {
            reader.close();
        }

        return data;
    }

    /**
     * <p>
     *     This will close the reader once finished reading or if an exception has been thrown
     * </p>
     * <p>
     *     This cannot read pure json arrays (if the json starts with '[' instead of '{')
     * </p>
     * @param reader {@link Reader} to read from
     * @return {@link SODataListImpl}
     * @see #readDataFromReader(Reader, boolean, String) 
     */
    public SODataListImpl readDataFromReader(Reader reader) throws ParseException, IOException {
        return readDataFromReader(reader, false, null);
    }

    /**
     * <p>
     *     This will close the reader once finished reading or if an exception has been thrown
     * </p>
     * @param reader {@link Reader} to read from
     * @param autoArray if the json starts with an array ("[...]"), it will parse this into a {@link SODataListImpl}
     * @param arrayKey the key, which the array should have in the created {@link SODataListImpl}
     * @return {@link SODataListImpl}
     */
    public SODataListImpl readDataFromReader(Reader reader, boolean autoArray, @Nullable String arrayKey) throws ParseException, IOException {
        SODataListImpl data;
        try {
            this.reader = reader;
            tracker = new ParseTracker();
            char c = nextFromStream(true);

            if (c == '[' && autoArray) {
                data = new SODataListImpl(new LinkedList<>());
                SAOEntryImpl entry = new SAOEntryImpl(arrayKey);
                c = readArrayFromReader(reader, c, entry);
                data.addEntry(entry); // we can use this here, since we created the SODataListImpl right before
            } else {
                data = readDataFromStream(c);
            }
        } finally {
            reader.close();
        }

        return data;
    }

    /**
     *
     * @param reader to read from
     * @return the Array, represented by the read json. for more information see {@link OldJsonParser}
     * @throws IOException
     * @throws ParseException
     */
    public ArrayList readArrayFromReader(Reader reader) throws IOException, ParseException {
        this.reader = reader;
        tracker = new ParseTracker();

        try {
            SimpleEntry entry = new SimpleEntry();
            char c = readArrayFromReader(reader, nextFromStream(true), entry);
            return (ArrayList) entry.getValue();
        } finally {
            reader.close();
        }

    }

    /**
     *
     * @param reader the reader to read from
     * @param c last read char, should be '['
     * @param entry the entry, the array should be saved to
     * @return might not return the correct char, if end is reached, see ,{@link #readValueFromStream(char, Entry, boolean)}
     * @throws IOException
     * @throws ParseException
     */
    private char readArrayFromReader(Reader reader, char c, Entry<?, Object> entry) throws IOException, ParseException {
        if (c != '[')
            throw new UnexpectedCharacterException(c, tracker);

        c = readValueFromStream(c, entry, true);

        return c;
    }


    /**
     * Does NOT close the writer after json has been written!
     *
     * @param data
     * @param writer
     * @throws IOException
     */
    public void writeData(AbstractData<?, ?> data, Writer writer) throws IOException {
        this.writer = writer;
        offset = new SpaceOffsetTracker(offsetString);
        if (data == null) data = new SODataListImpl(new ArrayList<>(0));
        writeJson(data);
    }

    public StringBuilder getJsonString(AbstractData<?, ?> data) {
        str = new StringBuilder();
        offset = new SpaceOffsetTracker(offsetString);
        return generateJsonString(data);
    }


    /**
     * Default: "  "
     * used for the indentation of the json string
     *
     * @param offsetString
     */
    public void setOffsetString(String offsetString) {
        this.offsetString = offsetString;
    }

    /**
     * Default: false
     * puts a single character after a number, to identify which type of number it is
     * Byte: B {@link OldJsonParser#BYTE_TOKEN}
     * Short: S {@link OldJsonParser#SHORT_TOKEN}
     * Integer: I {@link OldJsonParser#INTEGER_TOKEN}
     * Long: L {@link OldJsonParser#LONG_TOKEN}
     * Float: F {@link OldJsonParser#FLOAT_TOKEN}
     * Double: D {@link OldJsonParser#DOUBLE_TOKEN}
     *
     * @param identifyNumberValues
     */
    public void setIdentifyNumberValues(boolean identifyNumberValues) {
        this.identifyNumberValues = identifyNumberValues;
    }


    private SODataListImpl readDataFromStream(char c) throws IOException, ParseException {
        SODataListImpl data = new SODataListImpl(new LinkedList<>());

        if ((c) != '{') throw new UnexpectedCharacterException(c, tracker);
        if((c = nextFromStream(true)) == '}') return data; // Empty SODataListImpl

        while (true) {
            if ((c) != '"') throw new UnexpectedCharacterException(c, tracker);

            SAOEntryImpl e = new SAOEntryImpl(readKeyFromStream());
            if ((c = nextFromStream(false)) != ':') throw new UnexpectedCharacterException(c, tracker);
            c = readValueFromStream(nextFromStream(true), e);
            data.addEntry(e);
            if (c == ','){
                c = nextFromStream(true);
                continue;
            }
            else if (c == '}') break;
            else throw new UnexpectedCharacterException(c, tracker);
        }

        return data;
    }

    /**
     * reads a value from the {@link #reader}
     * <p>
     * The value might be
     * {@link String}
     * {@link SODataListImpl}
     * {@link ArrayList} (Arrays are always returned as ArrayLists)
     * {@link Number}
     *
     * @param c     the last read char in the stream
     * @param entry the entry, which value is to be set
     * @return the last read char in the stream
     * @throws ParseException
     * @throws IOException
     */
    private char readValueFromStream(char c, Entry<?, Object> entry) throws ParseException, IOException {
        return readValueFromStream(c, entry, false);
    }

    /**
     * reads a value from the {@link #reader}
     * <p>
     * The value might be: <br>
     * {@link String}<br>
     * {@link SODataListImpl}<br>
     * {@link ArrayList<Object>}&lt;Object&gt; (Arrays are always returned as ArrayLists) <br>
     * {@link Number}<br>
     *
     * @param c                             the last read char in the stream
     * @param entry                         the entry, which value is to be set
     * @param ignoreUnexpectedEndAfterArray used by {@link #readArrayFromReader(Reader)}
     * @return the last read char in the stream
     * @throws ParseException
     * @throws IOException
     */
    private char readValueFromStream(char c, Entry<?, Object> entry, boolean ignoreUnexpectedEndAfterArray) throws ParseException, IOException {

        if (c == '"') {
            entry.setValue(intrudeStringValueFrom());
            return nextFromStream(true);

        } else if (c == '{') {
            entry.setValue(readDataFromStream(c));
            return nextFromStream(true);

        } else if (c == '[') {
            c = nextFromStream(true);
            if (c == ']') {
                entry.setValue(new ArrayList<>());
                return nextFromStream(true, ignoreUnexpectedEndAfterArray);
            }

            ArrayList<Object> list = new ArrayList<>();
            while (c != ']') {

                if (c == ',') throw new UnexpectedCharacterException(c, tracker);

                SimpleEntry e = new SimpleEntry();
                c = readValueFromStream(c, e);
                list.add(e.getValue());

                if (c == ']') break;
                if (c != ',') throw new UnexpectedCharacterException(c, tracker);
                c = nextFromStream(true);
            }
            entry.setValue(list);
            return nextFromStream(true, ignoreUnexpectedEndAfterArray);

        } else {
            StringBuilder value = new StringBuilder();
            c = readRawValueFromStream(c, value);

            if (value.toString().trim().equalsIgnoreCase("true")) entry.setValue(true);
            else if (value.toString().trim().equalsIgnoreCase("false")) entry.setValue(false);
            else if (value.toString().trim().equalsIgnoreCase("null")) entry.setValue(null);
            else {
                try {
                    switch (value.charAt(value.length() - 1)) {
                        case BYTE_TOKEN:
                            entry.setValue(Byte.parseByte(value.substring(0, value.length() - 1)));
                            break;

                        case SHORT_TOKEN:
                            entry.setValue(Short.parseShort(value.substring(0, value.length() - 1)));
                            break;

                        case INTEGER_TOKEN:
                            entry.setValue(Integer.parseInt(value.substring(0, value.length() - 1)));
                            break;

                        case LONG_TOKEN:
                            entry.setValue(Long.parseLong(value.substring(0, value.length() - 1)));
                            break;

                        case FLOAT_TOKEN:
                            entry.setValue(Float.parseFloat(value.substring(0, value.length() - 1)));
                            break;

                        case DOUBLE_TOKEN:
                            entry.setValue(Double.parseDouble(value.substring(0, value.length() - 1)));
                            break;

                        default:
                            String vs = value.toString();
                            entry.setValue(NumberFormat.getNumberInstance(Locale.ENGLISH).parse(vs));
                            break;
                    }
                } catch (NumberFormatException | java.text.ParseException e) {
                    throw new ParseValueException(e, value.toString(), tracker);
                }
            }

            return c;
        }
    }

    /**
     * reads into the StringBuilder, until "}", "]" or "," is found
     *
     * @param c     first char to read
     * @param value the StringBuilder to append to
     * @return
     * @throws IOException
     * @throws UnexpectedEndException
     * @throws UnexpectedCharacterException
     */
    private char readRawValueFromStream(char c, StringBuilder value) throws IOException, UnexpectedEndException, UnexpectedCharacterException {
        value.append(c);

        int read;
        while ((read = reader.read()) != -1) {
            c = (char) read;
            if (c == '\n') {
                tracker.nextLine();
                c = nextFromStream(true);
                if (c == ']' || c == '}' || c == ',') return c;
                throw new UnexpectedCharacterException(c, tracker);
            }

            if (c == ']' || c == '}' || c == ',') return c;
            value.append(c);
        }

        throw new UnexpectedEndException();
    }

    private String intrudeStringValueFrom() throws UnexpectedCharacterException, IOException, UnexpectedEndException {
        StringBuilder str = new StringBuilder();

        int read = reader.read();
        char c;
        boolean escaped = false;
        while (read != -1) {
            c = (char) read;

            if (escaped) {
                escaped = false;
                switch (c) {
                    case 'n':
                        str.append('\n');
                        break;

                    case 'f':
                        str.append('\f');
                        break;

                    case 'r':
                        str.append('\r');
                        break;

                    case 't':
                        str.append('\t');
                        break;

                    case 'b':
                        str.append('\b');
                        break;

                    case '\\':
                        str.append('\\');
                        break;

                    case '"':
                        str.append('\"');
                        break;

                    case 'u':
                        StringBuilder esc = new StringBuilder(4);
                        for (int i = 0; i < 4; i++) {
                            if ((read = reader.read()) == -1) throw new UnexpectedEndException();
                            esc.append((char) read);
                        }

                        str.append((char) Integer.parseInt(esc.toString(), 16));
                        break;

                    default:
                        str.append(c);

                }
            } else {
                switch (c) {
                    case '\\':
                        escaped = true;
                        break;
                    case '"':
                        return str.toString();
                    case '\n':
                        throw new UnexpectedCharacterException(c, tracker);
                    default:
                        str.append(c);
                }
            }

            read = reader.read();
        }

        throw new UnexpectedEndException();
    }

    /**
     * reads until a " is found
     *
     * @return
     * @throws IOException
     * @throws UnexpectedCharacterException
     */
    private String readKeyFromStream() throws IOException, UnexpectedCharacterException, UnexpectedEndException {
        StringBuilder str = new StringBuilder();

        int read = reader.read();
        char c;
        while (read != -1) {
            c = (char) read;
            switch (c) {
                case '"':
                    return str.toString();
                case '\n':
                    throw new UnexpectedCharacterException(c, tracker);
                default:
                    str.append(c);
            }

            read = reader.read();
        }

        throw new UnexpectedEndException();
    }

    /**
     * reads until a character, which is not space, tab or a new line is found
     *
     * @param allowNewLine
     * @return
     * @throws IOException
     * @throws UnexpectedEndException       when stream ends
     * @throws UnexpectedCharacterException
     */
    private char nextFromStream(boolean allowNewLine) throws IOException, UnexpectedEndException, UnexpectedCharacterException {
        return nextFromStream(allowNewLine, false);
    }

    /**
     * reads until a character, which is not space, tab or a new line is found
     * <p>
     * if end of reader is reached and ignoreUnexpectedEnd is true, 'a' is returned
     *
     * @param allowNewLine
     * @param ignoreUnexpectedEnd
     * @return
     * @throws IOException
     * @throws UnexpectedEndException       when stream ends and ignoreUnexpectedEnd is false
     * @throws UnexpectedCharacterException
     */
    private char nextFromStream(boolean allowNewLine, boolean ignoreUnexpectedEnd) throws IOException, UnexpectedEndException, UnexpectedCharacterException {
        int read = reader.read();
        char c;
        while (read != -1) {
            c = (char) read;

            switch (c) {
                case ' ':
                case '\t':
                    break;
                case '\n':
                    if (allowNewLine) tracker.nextLine();
                    else throw new UnexpectedCharacterException(c, tracker);
                    break;
                default:
                    if (c <= '\u001F') break; //Apparently these characters are there too :c
                    return c;
            }

            read = reader.read();
        }

        if (ignoreUnexpectedEnd)
            return 'a';

        throw new UnexpectedEndException();
    }


    /**
     * Creates a beautiful json string of a {@link SODataListImpl}
     *
     * @param data
     * @return the json of a data as StringBuilder
     */
    private StringBuilder generateJsonString(@NotNull AbstractData<?, ?> data) {
        str.append('{');
        offset.add();

        boolean first = true;
        for (Entry<?, ?> entry : data) {
            if (!first) str.append(',');
            else first = false;

            str.append('\n').append(offset);
            str.append('"').append(entry.getKey()).append("\": ");

            jsonValueJsonString(entry.getValue());

        }

        str.append('\n');
        offset.remove();
        str.append(offset).append('}');


        return str;
    }


    private void jsonValueJsonString(@Nullable Object value) {
        if (value == null) {
            str.append("null");

        } else if (value instanceof Datable) {
            generateJsonString(((Datable) value).getData());

        } else if (value instanceof SimpleDatable) {
            jsonValueJsonString(((SimpleDatable) value).simplify());

        } else if (value instanceof String) {
            str.append('\"');
            ParseHelper.escape2((String) value, str);
            str.append('\"');

        } else if (value instanceof Boolean) {
            str.append(value.toString());

        } else if (value instanceof Integer) {
            str.append(value.toString());
            if (identifyNumberValues) str.append(INTEGER_TOKEN);

        } else if (value instanceof Long) {
            str.append(value.toString());
            if (identifyNumberValues) str.append(LONG_TOKEN);

        } else if (value instanceof Byte) {
            str.append(value);
            if (identifyNumberValues) str.append(BYTE_TOKEN);

        } else if (value instanceof Short) {
            str.append(value);
            if (identifyNumberValues) str.append(SHORT_TOKEN);

        } else if (value instanceof Double) {
            str.append(value.toString());
            if (identifyNumberValues) str.append(DOUBLE_TOKEN);

        } else if (value instanceof Float) {
            str.append(value.toString());
            if (identifyNumberValues) str.append(FLOAT_TOKEN);

        } else if (value instanceof Collection) {
            str.append('[').append('\n');
            offset.add();

            boolean first = true;
            for (Object o : (Collection) value) {
                if (!first) str.append(',').append('\n');
                else first = false;
                str.append(offset);
                jsonValueJsonString(o);
            }

            str.append('\n');
            offset.remove();
            str.append(offset).append(']');

        } else if (value instanceof Object[]) {
            jsonValueJsonString((Object[]) value);

        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                byte[] a = (byte[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            } else if (value instanceof short[]) {
                short[] a = (short[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            } else if (value instanceof int[]) {
                int[] a = (int[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            } else if (value instanceof long[]) {
                long[] a = (long[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            } else if (value instanceof float[]) {
                float[] a = (float[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            } else if (value instanceof double[]) {
                double[] a = (double[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                jsonValueJsonString(o);

            }
        } else {
            //If the Object is none of the above, a simple string is added instead
            str.append('"');
            ParseHelper.escape2(value.toString(), str);
            str.append('"');
        }
    }

    private void jsonValueJsonString(@Nullable Object[] value) {
        str.append('[').append('\n');
        offset.add();

        boolean first = true;
        for (Object o : value) {
            if (!first) str.append(',').append('\n');
            else first = false;
            str.append(offset);
            jsonValueJsonString(o);
        }

        str.append('\n');
        offset.remove();
        str.append(offset).append(']');
    }


    /**
     * Creates a beautiful json string of a {@link AbstractData}
     *
     * @param data
     * @return the json of a data as StringBuilder
     */
    private void writeJson(@Nullable AbstractData<?, ?> data) throws IOException {
        if (data == null) data = new SODataListImpl(new ArrayList<>(0));
        writer.append('{');
        offset.add();

        boolean first = true;
        for (Entry<?, ?> entry : data) {
            if (!first) writer.append(',');
            else first = false;

            writer.append('\n').append(offset.toString());
            writer.append('"').append(Objects.toString(entry.getKey())).append("\": ");

            writeJsonValue(entry.getValue());

        }

        writer.append('\n');
        offset.remove();
        writer.append(offset.toString()).append('}');
    }


    private void writeJsonValue(@Nullable Object value) throws IOException {
        if (value == null) {
            writer.append("null");

        } else if (value instanceof Datable) {
            writeJson(((Datable) value).getData());

        } else if (value instanceof SimpleDatable) {
            writeJsonValue(((SimpleDatable) value).simplify());

        } else if (value instanceof String) {
            writer.append('\"');
            ParseHelper.escape2((String) value, writer);
            writer.append('\"');

        } else if (value instanceof Boolean) {
            writer.append(value.toString());

        } else if (value instanceof Integer) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(INTEGER_TOKEN);

        } else if (value instanceof Long) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(LONG_TOKEN);

        } else if (value instanceof Byte) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(BYTE_TOKEN);

        } else if (value instanceof Short) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(SHORT_TOKEN);

        } else if (value instanceof Double) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(DOUBLE_TOKEN);

        } else if (value instanceof Float) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(FLOAT_TOKEN);

        } else if (value instanceof Collection) {
            writer.append('[').append('\n');
            offset.add();

            boolean first = true;
            for (Object o : (Collection) value) {
                if (!first) writer.append(',').append('\n');
                else first = false;
                writer.append(offset.toString());
                writeJsonValue(o);
            }

            writer.append('\n');
            offset.remove();
            writer.append(offset.toString()).append(']');

        } else if (value instanceof Object[]) {
            writeJsonValue((Object[]) value);

        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                byte[] a = (byte[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            } else if (value instanceof short[]) {
                short[] a = (short[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            } else if (value instanceof int[]) {
                int[] a = (int[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            } else if (value instanceof long[]) {
                long[] a = (long[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            } else if (value instanceof float[]) {
                float[] a = (float[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            } else if (value instanceof double[]) {
                double[] a = (double[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(o);

            }

        } else {
            //If the Object is none of the above, a simple string is added instead
            writer.append('"');
            ParseHelper.escape2(value.toString(), writer);
            writer.append('"');
        }
    }

    private void writeJsonValue(@NotNull Object[] value) throws IOException {
        writer.append('[').append('\n');
        offset.add();

        boolean first = true;
        for (Object o : value) {
            if (!first) writer.append(',').append('\n');
            else first = false;
            writer.append(offset.toString());
            writeJsonValue(o);
        }

        writer.append('\n');
        offset.remove();
        writer.append(offset.toString()).append(']');
    }


}
