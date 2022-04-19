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

import me.linusdev.data.AbstractData;
import me.linusdev.data.Datable;
import me.linusdev.data.SimpleDatable;
import me.linusdev.data.entry.Entry;
import me.linusdev.data.implemantations.DataListImpl;
import me.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static me.linusdev.data.parser.ParseHelper.*;

public class JsonParser2 {

    //Configurable stuff
    private @NotNull String offsetString = "  ";
    private boolean escapeForwardSlash = false;
    private boolean identifyNumberValues = false;

    /*
     *
     *              Config setter
     *
     */

    public void setOffsetString(@NotNull String offsetString) {
        this.offsetString = offsetString;
    }

    public void setEscapeForwardSlash(boolean escapeForwardSlash) {
        this.escapeForwardSlash = escapeForwardSlash;
    }

    public void setIdentifyNumberValues(boolean identifyNumberValues) {
        this.identifyNumberValues = identifyNumberValues;
    }

    /*
     *
     *              Stream to Data
     *
     */

    public SOData readDataFromStream(@NotNull SOData data, @NotNull InputStream stream) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(new InputStreamReader(stream));
        tokenizer.nextToken();
        return null;
    }


    /*
     *
     *              Data to String
     *
     */

    /**
     *
     * @param data {@link AbstractData} to write to a {@link StringBuffer}
     * @return {@link StringBuffer#toString()}
     * @throws IOException {@link IOException} while writing
     */
    public String writeDataToString(@Nullable AbstractData<?, ?> data) throws IOException {
        return writeDataToStringBuffer(data).toString();
    }

    /**
     *
     * @param data {@link AbstractData} to write to a {@link StringBuffer}
     * @return {@link StringBuffer}
     * @throws IOException {@link IOException} while writing
     */
    public StringBuffer writeDataToStringBuffer(@Nullable AbstractData<?, ?> data) throws IOException {
        StringWriter writer = new StringWriter(data == null ? 10 : data.size() * 10);
        writeData(writer, data);
        return writer.getBuffer();
    }

    /**
     *
     * @param data {@link AbstractData} to write. {@code null} will write an empty Data: "{}"
     * @param writer {@link Writer} to write to
     * @throws IOException {@link IOException} while writing
     */
    public void writeData(@NotNull Writer writer, @Nullable AbstractData<?, ?> data) throws IOException {
        SpaceOffsetTracker offset = new SpaceOffsetTracker(offsetString);
        writeJson(writer, offset, data);
    }

    private void writeJson(@NotNull Writer writer, @NotNull SpaceOffsetTracker offset, @Nullable AbstractData<?, ?> data) throws IOException {
        if (data == null) data = new DataListImpl(new ArrayList<>(0));
        writer.append('{');
        offset.add();

        boolean first = true;
        for (Entry<?, ?> entry : data) {
            if (!first) writer.append(',');
            else first = false;

            writer.append('\n').append(offset.toString());
            writer.append('"').append(Objects.toString(entry.getKey())).append("\": ");

            writeJsonValue(writer, offset, entry.getValue());

        }

        writer.append('\n');
        offset.remove();
        writer.append(offset.toString()).append('}');
    }

    private void writeJsonValue(@NotNull Writer writer, @NotNull SpaceOffsetTracker offset, @Nullable Object value) throws IOException {
        if (value == null) {
            writer.append("null");

        } else if (value instanceof Datable) {
            writeJson(writer, offset, ((Datable) value).getData());

        } else if (value instanceof SimpleDatable) {
            writeJsonValue(writer, offset, ((SimpleDatable) value).simplify());

        } else if (value instanceof String) {
            writer.append('\"');
            if (escapeForwardSlash) ParseHelper.escapeWithForwardSlash((String) value, writer);
            else ParseHelper.escape((String) value, writer);
            writer.append('\"');

        } else if (value instanceof Boolean) {
            writer.append(value.toString());

        } else if (value instanceof Integer) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(INTEGER_ID);

        } else if (value instanceof Long) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(LONG_ID);

        } else if (value instanceof Byte) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(BYTE_ID);

        } else if (value instanceof Short) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(SHORT_ID);

        } else if (value instanceof Double) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(DOUBLE_ID);

        } else if (value instanceof Float) {
            writer.append(value.toString());
            if (identifyNumberValues) writer.append(FLOAT_ID);

        } else if (value instanceof Collection) {
            writer.append('[').append('\n');
            offset.add();

            boolean first = true;
            for (Object o : (Collection) value) {
                if (!first) writer.append(',').append('\n');
                else first = false;
                writer.append(offset.toString());
                writeJsonValue(writer, offset, o);
            }

            writer.append('\n');
            offset.remove();
            writer.append(offset.toString()).append(']');

        } else if (value instanceof Object[]) {
            writeJsonValue(writer, offset, (Object[]) value);

        } else if (value.getClass().isArray()) {
            if (value instanceof byte[]) {
                byte[] a = (byte[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            } else if (value instanceof short[]) {
                short[] a = (short[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            } else if (value instanceof int[]) {
                int[] a = (int[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            } else if (value instanceof long[]) {
                long[] a = (long[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            } else if (value instanceof float[]) {
                float[] a = (float[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            } else if (value instanceof double[]) {
                double[] a = (double[]) value;
                Object[] o = new Object[a.length];
                for (int i = 0; i < a.length; i++) o[i] = a[i];
                writeJsonValue(writer, offset, o);

            }

        } else {
            //If the Object is none of the above, a simple string is added instead
            writer.append('"');
            ParseHelper.escapeWithForwardSlash(value.toString(), writer);
            writer.append('"');
        }
    }

    private void writeJsonValue(@NotNull Writer writer, @NotNull SpaceOffsetTracker offset, @NotNull Object[] value) throws IOException {
        writer.append('[').append('\n');
        offset.add();

        boolean first = true;
        for (Object o : value) {
            if (!first) writer.append(',').append('\n');
            else first = false;
            writer.append(offset.toString());
            writeJsonValue(writer, offset, o);
        }

        writer.append('\n');
        offset.remove();
        writer.append(offset.toString()).append(']');
    }

}
