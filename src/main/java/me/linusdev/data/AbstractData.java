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

package me.linusdev.data;

import me.linusdev.data.entry.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @param <K> key
 * @param <V> value
 */
public interface AbstractData<K, V> {

    /**
     * Adds a new {@link Entry}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits
     * (depending on the implementation)
     * </p>
     *
     * @param key key
     * @param value value
     * @return {@code true} if a new {@link Entry} was added, {@code false} otherwise
     */
    boolean add(@NotNull K key, @Nullable V value);

    /**
     * Adds a new {@link Entry} if given value is not {@code null}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits
     * (depending on the implementation)
     * </p>
     *
     * @param key key
     * @param value value
     * @return {@code true} if a new {@link Entry} was added, {@code false} otherwise
     */
    default boolean addIfNotNull(@NotNull K key, @Nullable V value){
        if(value != null){
            add(key, value);
            return true;
        }
        return false;
    }

    /**
     * If no {@link Entry} for given key exists, a new {@link Entry} is added.<br>
     * If an {@link Entry} with given key exists, it's value is changed to given value.
     * @param key {@link K key}
     * @param value {@link V value}
     * @return Old mapping for given key or {@code null} if there was no old mapping for given key.
     */
    default V addOrReplace(@NotNull K key, @Nullable V value) {
        Entry<K, V> entry = getEntry(key);

        if(entry == null) {
            add(key, value);
            return null;
        }

        V old = entry.getValue();
        entry.setValue(value);
        return old;
    }

    /**
     *
     * @param key {@link K key}
     * @return {@link Entry} with given key or {@code null}, if no such {@link Entry} exists.
     */
    @Nullable Entry<K, V> getEntry(@NotNull K key);

    /**
     *
     * Gets {@link V value} for given key or
     * <ul>
     *      <li>defaultObject, if no {@link Entry} for given key exists</li>
     *      <li>defaultObjectIfNull, if {@link V value} for given key is {@code null}</li>
     * </ul>
     *
     * @param key {@link K key}
     * @param defaultObject {@link V} default object to return, if no {@link Entry} for given key exists.
     * @param defaultObjectIfNull {@link V} default object to return, if {@link V value} for given key is {@code null}.
     * @return {@link V value} for given key or a default value as specified above.
     */
    default @Nullable V get(K key, V defaultObject, V defaultObjectIfNull) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        if(entry.getValue() == null) return defaultObjectIfNull;
        return entry.getValue();
    }

    /**
     * @param key {@link K key}
     * @param defaultObject {@link V} default object to return, if no {@link Entry} for given key exists.
     * @return {@link V value} for given key or a defaultObject if no {@link Entry} with given key exists.
     */
    default @Nullable V get(K key, V defaultObject) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        return entry.getValue();
    }

    /**
     *
     * @param key {@link K key}
     * @return {@link V value} or {@code null} if no {@link Entry} for given key exists.
     */
    default @Nullable V get(K key) {
        return get(key, null);
    }

    @Nullable StringBuilder getJsonString();
}
