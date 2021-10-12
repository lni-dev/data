package me.linusdev.data.parser;

import me.linusdev.data.*;
import me.linusdev.data.parser.exceptions.ParseException;
import me.linusdev.data.parser.exceptions.ParseValueException;
import me.linusdev.data.parser.exceptions.UnexpectedCharacterException;
import me.linusdev.data.parser.exceptions.UnexpectedEndException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;


/**
 * This class is used to parse {@link Data} to a json string and vice versa
 * <br><br>
 *
 *
 * {@link Data} to json-string:<br>
 * Can parse {@link Boolean}, {@link Byte}, {@link Short}, {@link Integer},
 * {@link Long}, {@link Float}, {@link Double}, {@link String} as well as {@link Object}[],
 * any primitive type array and {@link Collection}
 * <br><br>
 *
 *
 * json-string to {@link Data}:<br>
 * false/true to {@link Boolean} (ignores case)<br>
 * null to {@code null} (ignores case)<br>
 * Integer Numbers (1, 4, 5, ...) to {@link Long}
 * (if {@link #setIdentifyNumberValues(boolean)} is set to false (standard) while converting to string(!))<br>
 * Decimal Numbers (5.6, ...) to {@link Double}
 * (if {@link #setIdentifyNumberValues(boolean)} is set to false (standard) while converting to string(!))<br>
 * "strings" to {@link String}<br>
 * Arrays ([...]) to {@link ArrayList<Object>}<br>
 * any other values are not supported and will most like cause an {@link ParseException}
 *
 *
 */
public class JsonParser {

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
    private boolean escapeForwardSlash = false;
    private boolean identifyNumberValues = false;

    public JsonParser() {

    }

    public Data readDataFroResourceFile(String resource) throws IOException, ParseException, NullPointerException {
        Data data;
        try {
            InputStream in = JsonParser.class.getClassLoader().getResourceAsStream(resource);
            if(in == null) return null;
            reader = new BufferedReader(new InputStreamReader(in));
            tracker = new ParseTracker();
            data = readDataFromStream(nextFromStream(true));
        } finally {
            reader.close();
        }

        return data;
    }

    public Data readDataFromFile(Path filePath) throws IOException, ParseException {
        Data data;
        try {
            reader = Files.newBufferedReader(filePath);
            tracker = new ParseTracker();
            data = readDataFromStream(nextFromStream(true));
        } finally {
            reader.close();
        }

        return data;
    }

    public Data readDataFromReader(Reader reader) throws ParseException, IOException {
        Data data;
        try {
            this.reader = reader;
            tracker = new ParseTracker();
            data = readDataFromStream(nextFromStream(true));
        } finally {
            reader.close();
        }

        return data;
    }


    /**
     * Does NOT close the writer after json has been written!
     * @param data
     * @param writer
     * @throws IOException
     */
    public void writeData(Data data, Writer writer) throws IOException {
        this.writer = writer;
        offset = new SpaceOffsetTracker(offsetString);
        if(data == null) data = new Data(0);
        writeJson(data);
    }

    public StringBuilder getJsonString(Data data) {
        str = new StringBuilder();
        offset = new SpaceOffsetTracker(offsetString);
        return generateJsonString(data);
    }



    /**
     * Default: "  "
     * used for the indentation of the json string
     * @param offsetString
     */
    public void setOffsetString(String offsetString) {
        this.offsetString = offsetString;
    }

    /**
     * Default: false
     * Whether "/" should be escaped or not
     * @param escapeForwardSlash
     */
    public void setEscapeForwardSlash(boolean escapeForwardSlash) {
        this.escapeForwardSlash = escapeForwardSlash;
    }

    /**
     * Default: false
     * puts a single character after a number, to identify which type of number it is
     * Byte: B {@link JsonParser#BYTE_TOKEN}
     * Short: S {@link JsonParser#SHORT_TOKEN}
     * Integer: I {@link JsonParser#INTEGER_TOKEN}
     * Long: L {@link JsonParser#LONG_TOKEN}
     * Float: F {@link JsonParser#FLOAT_TOKEN}
     * Double: D {@link JsonParser#DOUBLE_TOKEN}
     * @param identifyNumberValues
     */
    public void setIdentifyNumberValues(boolean identifyNumberValues) {
        this.identifyNumberValues = identifyNumberValues;
    }




















    private Data readDataFromStream(char c) throws IOException, ParseException {
        Data data = new Data(1);

        if ((c) != '{') throw new UnexpectedCharacterException(c, tracker);

        while (true) {
            if ((c = nextFromStream(true)) != '"') throw new UnexpectedCharacterException(c, tracker);

            Entry e = new Entry(readKeyFromStream());
            if ((c = nextFromStream(false)) != ':') throw new UnexpectedCharacterException(c, tracker);
            c = readValueFromStream(nextFromStream(true), e);
            data.addEntry(e);
            if (c == ',') continue;
            else if (c == '}') break;
            else throw new UnexpectedCharacterException(c, tracker);
        }

        return data;
    }

    /**
     * reads a value from the {@link #reader}
     *
     * The value might be
     * {@link String}
     * {@link Data}
     * {@link ArrayList} (Arrays are always returned as ArrayLists)
     * {@link Number}
     *
     * @param c the last read char in the stream
     * @param entry the entry, which value is to be set
     * @return the last read char in the stream
     * @throws ParseException
     * @throws IOException
     */
    private char readValueFromStream(char c, SimpleEntry entry) throws ParseException, IOException {

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
                return nextFromStream(true);
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
            return nextFromStream(true);

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
     * @throws UnexpectedEndException
     * @throws UnexpectedCharacterException
     */
    private char nextFromStream(boolean allowNewLine) throws IOException, UnexpectedEndException, UnexpectedCharacterException {
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

        throw new UnexpectedEndException();
    }






















    /**
     * Creates a beautiful json string of a {@link Data}
     *
     * @param data
     * @return the json of a data as StringBuilder
     */
    private StringBuilder generateJsonString(@NotNull Data data) {
        str.append('{');
        offset.add();

        boolean first = true;
        for (Entry entry : data.getEntries()) {
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
            generateJsonString( ((Datable) value).getData());

        } else if (value instanceof SimpleDatable) {
            jsonValueJsonString( ((SimpleDatable) value).simplify());

        } else if (value instanceof String) {
            str.append('\"');
            if (escapeForwardSlash) ParseHelper.escapeWithForwardSlash((String) value, str);
            else ParseHelper.escape((String) value, str);
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
        }else {
            //If the Object is none of the above, a simple string is added instead
            str.append('"');
            ParseHelper.escapeWithForwardSlash(value.toString(), str);
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
     * Creates a beautiful json string of a {@link Data}
     *
     * @param data
     * @return the json of a data as StringBuilder
     */
    private void writeJson(@Nullable Data data) throws IOException {
        if(data == null) data = new Data(0);
        writer.append('{');
        offset.add();

        boolean first = true;
        for (Entry entry : data.getEntries()) {
            if (!first) writer.append(',');
            else first = false;

            writer.append('\n').append(offset.toString());
            writer.append('"').append(entry.getKey()).append("\": ");

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
            if (escapeForwardSlash) ParseHelper.escapeWithForwardSlash((String) value, writer);
            else ParseHelper.escape((String) value, writer);
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

        }else {
            //If the Object is none of the above, a simple string is added instead
            writer.append('"');
            ParseHelper.escapeWithForwardSlash(value.toString(), writer);
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
