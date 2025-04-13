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

package de.linusdev.data;

import de.linusdev.data.entry.Entry;
import de.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * a data wrapper wraps a single object inside a {@link AbstractData}.
 * <br><br>
 * When parsed a normal {@link AbstractData} will be parsed to a json-object. That means it will start with
 * a "{".
 * <br> This is not always useful. For example when parsing an array to a json-array without a json-object around it.
 * That the use case of the {@link DataWrapper}. It will parse the object it is wrapping without adding a json-object around it.
 * <br><br>
 * That is why a {@link DataWrapper} can only have a single {@link Entry} at a time. Adding or removing this will throw an
 * {@link UnsupportedOperationException}
 *
 * @see SOData#wrap(Object)
 */
public interface DataWrapper<K, V> extends ContentOnlyData<K, V> {

    /**
     * @return the key of the entry inside this {@link DataWrapper}
     */
    K getKey();

    @Override
    @NotNull Entry<K, V> getEntry(@NotNull K key);

    default void set(@NotNull V obj){
        getEntry(getKey()).setValue(obj);
    }

    default V get(){
        return getEntry(getKey()).getValue();
    }

    @Override
    default @NotNull AbstractData<K, V> add(@NotNull K key, @Nullable V value) {
        throw new UnsupportedOperationException("This data can only have a single entry, use the set method instead");
    }

    @Override
    default void addEntry(@NotNull Entry<K, V> entry) {
        throw new UnsupportedOperationException("This data can only have a single entry, use the set method instead");
    }

    @Override
    default @Nullable Entry<K, V> remove(@NotNull K key) {
        throw new UnsupportedOperationException("This data can only have a single entry, which cannot be removed. Use the set method instead");
    }

    @Override
    default boolean isEmpty() {
        return false;
    }

    @Override
    default int size() {
        return 1;
    }

    @NotNull
    @Override
    default Iterator<Entry<K, V>> iterator() {
        return new Iterator<>() {

            boolean retrieved = false;

            @Override
            public boolean hasNext() {
                return !retrieved;
            }

            @Override
            public Entry<K, V> next() {
                if(retrieved) throw new NoSuchElementException();
                retrieved = true;
                return getEntry(getKey());
            }
        };
    }
}
