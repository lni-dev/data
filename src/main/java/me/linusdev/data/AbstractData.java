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

import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.entry.Entry;
import me.linusdev.data.implemantations.DataMapImpl;
import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @param <K> key
 * @param <V> value
 */
public interface AbstractData<K, V> extends Iterable<Entry<K, V>>, Datable{

    /**
     * Adds a new {@link Entry}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits.
     * Depending on the implementation, this might even override existing mappings
     * (for Example {@link DataMapImpl DataMapImpl}).
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
     * This method might not check, if a {@link Entry} with given key already exits.
     * Depending on the implementation, this might even override existing mappings
     * (for Example {@link DataMapImpl DataMapImpl}).
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
     * Adds a new {@link Entry}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits
     * (depending on the implementation)
     * </p>
     *
     * @param entry entry to add
     */
    void addEntry(@NotNull Entry<K, V> entry);

    /**
     * <p>
     *     Removes {@link Entry} with given key. If no such entry exists nothing happens.
     * </p>
     * @param key key for the entry to remove
     * @return the removed {@link Entry} or {@code null} if no entry was removed.
     */
    @Nullable Entry<K, V> remove(@NotNull K key);

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
    default @Nullable V get(@NotNull K key, @Nullable V defaultObject, @Nullable V defaultObjectIfNull) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        if(entry.getValue() == null) return defaultObjectIfNull;
        return entry.getValue();
    }

    /**
     * @see #get(Object, Object, Object)
     */
    @Contract("_, !null, !null -> !null")
    default @Nullable V getOrDefault(@NotNull K key, @Nullable V defaultObject, @Nullable V defaultObjectIfNull) {
        return get(key, defaultObject, defaultObjectIfNull);
    }

    /**
     *
     *
     * @param key {@link  K key}
     * @param defaultObject {@link V} default object to return, if no {@link Entry} for given key exists or value is {@code null}
     * @return {@link V value} for given key or defaultObject if no {@link Entry} for given key exists or value is {@code null}.
     * @see #get(Object, Object, Object)
     */
     @Contract("_, !null -> !null")
    default @Nullable V getOrDefaultBoth(@NotNull K key, @Nullable V defaultObject) {
        return get(key, defaultObject, defaultObject);
    }

    /**
     * @param key {@link K key}
     * @param defaultObject {@link V} default object to return, if no {@link Entry} for given key exists.
     * @return {@link V value} for given key or a defaultObject if no {@link Entry} with given key exists.
     */
    default @Nullable V get(@NotNull K key, @Nullable V defaultObject) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        return entry.getValue();
    }

    /**
     * @see #get(Object, Object)
     */
    default @Nullable V getOrDefault(@NotNull K key, @Nullable V defaultObject) {
        return get(key, defaultObject);
    }

    /**
     *
     * @param key {@link K key}
     * @return {@link V value} or {@code null} if no {@link Entry} for given key exists.
     */
    default @Nullable V get(@NotNull K key) {
        return get(key, null);
    }

    /**
     *
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     * If the value returned by {@link #get(Object)} with given key is {@code null}, {@link Converter#convert(Object)} with {@code null} is called!
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    default  <C extends V, R> R getAndConvert(@NotNull K key, @NotNull Converter<C, R> converter){
        C convertible = (C) get(key);
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param defaultObject object to return if {@link #get(Object)} with given key is {@code null}
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(Object)} with given is {@code null} or if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Contract("_, _, !null -> !null")
    default <C extends V, R> R getAndConvert(@NotNull K key, @NotNull Converter<C, R> converter, @Nullable R defaultObject){
        C convertible = (C) get(key);
        if(convertible == null) return defaultObject;
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     * If the value returned by {@link #get(Object)} with given key is {@code null}, {@link ExceptionConverter#convert(Object)} with {@code null} is called!
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param <C> the convertible type
     * @param <R> the result type
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return result {@link R} or {@code null} if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     * @throws E if {@link ExceptionConverter#convert(Object)} throws an Exception
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <C extends V, R, E extends Throwable> R getAndConvert(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
        C convertible = (C) get(key);
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param defaultObject object to return if {@link #get(Object)} with given key is {@code null}
     * @param <C> the convertible type
     * @param <R> the result type
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(Object)} with given is {@code null} or if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     * @throws E if {@link ExceptionConverter#convert(Object)} throws an Exception
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default  <C extends V, R, E extends Exception> R getAndConvertWithException(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter, @Nullable R defaultObject) throws E {
        C convertible = (C) get(key);
        if(convertible == null) return defaultObject;
        return converter.convert(convertible);
    }

    /**
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @return {@link List} of {@link Object}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     */
    @SuppressWarnings("unchecked")
    default @Nullable List<Object> getList(@NotNull K key) {
        V value = get(key);
        return (List<Object>) value;
    }

    /**
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @return {@link List} of {@link Object} or defaultList if {@link #get(Object)} for given key returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null -> !null")
    default @Nullable List<Object> getList(@NotNull K key, @Nullable List<Object> defaultList) {
        V value = get(key);
        return value == null ? defaultList : (List<Object>) value;
    }

    /**
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @param converter to convert from {@link C value contained in the implemantations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implemantations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implemantations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
     */
    @SuppressWarnings("unchecked")
    default <C, R> @Nullable ArrayList<R> getListAndConvert(@NotNull K key, @NotNull Converter<C, R> converter) {
        List<Object> list = getList(key);
        if(list == null) return null;

        ArrayList<R> returnList = new ArrayList<>(list.size());
        for(Object o : list) {
            returnList.add(converter.convert((C) o));
        }

        return returnList;
    }

    /**
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @param converter to convert from {@link C value contained in the implemantations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implemantations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implemantations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
     * @throws E if the converter throws this exception
     */
    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @Nullable ArrayList<R> getListAndConvertWithException(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
        List<Object> list = getList(key);
        if(list == null) return null;

        ArrayList<R> returnList = new ArrayList<>(list.size());
        for(Object o : list) {
            returnList.add(converter.convert((C) o));
        }

        return returnList;
    }

    /**
     * <p>
     *     Each element from the implemantations - returned by {@link #getList(Object)} with given key - will be set to {@code null},
     *     after it has been converted and stored in the implemantations that will be returned. After every element of the former implemantations has been converted,
     *     {@link List#clear()} will be called.
     * </p>
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @param converter to convert from {@link C value contained in the implemantations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implemantations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implemantations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
     */
    @SuppressWarnings("unchecked")
    default <C, R> @Nullable ArrayList<R> getListAndConvertAndFreeMemory(@NotNull K key, @NotNull Converter<C, R> converter) {
        List<Object> list = getList(key);
        if(list == null) return null;

        ArrayList<R> returnList = new ArrayList<>(list.size());
        for(int i = 0; i < list.size(); i++) {
            returnList.add(converter.convert((C) list.get(i)));
            list.set(i, null); //Free memory
        }

        list.clear();
        return returnList;
    }

    /**
     * <p>
     *     If an entry with given key exists, it will be processed by given consumer and {@code true} will be returned.<br>
     *     If an entry with given key does not exists, {@code false} will be returned.<br>
     *     <br>
     *     The entry will <b>NOT</b> be removed from this {@link AbstractData}
     *
     * </p>
     * @param key the key for the entry
     * @param consumer consumer to process the entry, if it exists
     * @param <C> type to cast to
     * @return {@code true} if an entry with given key exists, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    default  <C> boolean processIfContained(@NotNull String key, @NotNull Consumer<C> consumer){
        for(Entry<K, V> entry : this){
            if(entry.getKey().equals(key)){
                consumer.accept((C) entry.getValue());
                return true;
            }
        }

        return false;
    }


    /**
     *
     * @return {@code true} if this data does not contain any entries, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     *
     * @return current amount of {@link Entry entries} contained.
     */
    int size();

    default @Nullable StringBuilder toJsonString() {
        return new JsonParser().getJsonString(this);
    }

    @Override
    default AbstractData<K, V> getData() {
        return this;
    }
}
