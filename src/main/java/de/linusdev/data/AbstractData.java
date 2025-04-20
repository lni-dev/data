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
import de.linusdev.data.functions.ExceptionSupplier;
import de.linusdev.data.functions.ValueFactory;
import de.linusdev.data.implemantations.SODataMapImpl;
import de.linusdev.data.parser.JsonParser;
import de.linusdev.lutils.interfaces.Converter;
import de.linusdev.lutils.interfaces.ExceptionConverter;
import de.linusdev.lutils.optional.Container;
import de.linusdev.lutils.optional.OptionalValue;
import de.linusdev.lutils.optional.impl.BasicContainer;
import de.linusdev.lutils.other.NumberUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link AbstractData} can store key-value pairs.
 * <br><br>
 * These can be parsed to a json-string using {@link JsonParser}.
 * @param <K> key
 * @param <V> value
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface AbstractData<K, V> extends Iterable<Entry<K, V>>, Datable{

    public static final JsonParser PARSER = new JsonParser();

    /**
     * Adds a new {@link Entry}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits.
     * Depending on the implementation, this might even override existing mappings
     * (for Example {@link SODataMapImpl SODataMapImpl}).
     * </p>
     *
     * @param key key
     * @param value value
     * @return this
     */
    @NotNull AbstractData<K, V> add(@NotNull K key, @Nullable V value);

    /**
     * Adds a new {@link Entry} if given value is not {@code null}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given key already exits.
     * Depending on the implementation, this might even override existing mappings
     * (for Example {@link SODataMapImpl SODataMapImpl}).
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
     * Adds a new {@link Entry} with given {@code key} and {@link OptionalValue#get()} if given {@code optionalValue} {@link OptionalValue#exists() exists}. <br>
     * <p>
     * This method might not check, if a {@link Entry} with given {@code key} already exits.
     * Depending on the implementation, this might even override existing mappings
     * (for Example {@link SODataMapImpl SODataMapImpl}).
     * </p>
     *
     * @param key {@link K} key
     * @param optionalValue {@link OptionalValue}
     * @return {@code true} if a new {@link Entry} was added, {@code false} otherwise
     */
    default boolean addIfOptionalExists(@NotNull K key, @NotNull OptionalValue<? extends V> optionalValue) {
        if(optionalValue.exists()) {
            add(key, optionalValue.get());
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
     * @param key {@link K key}
     * @return {@link OptionalValue}
     * @param <C> type tp cast to
     * @throws ClassCastException if the value for given key is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C> @NotNull  OptionalValue<C> getOptionalValue(@NotNull K key) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return OptionalValue.of();
        return OptionalValue.of((C) entry.getValue());
    }

    /**
     *
     * @param key {@link K key}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}.
     * @return {@link OptionalValue}
     * @param <C> type tp cast to
     * @param <R> type your converter converts to
     * @throws ClassCastException if the value for given key is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C, R> @NotNull OptionalValue<R> getOptionalValueAndConvert(@NotNull K key, @NotNull Converter<C, R> converter) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return OptionalValue.of();
        return OptionalValue.of(converter.convert((C) entry.getValue()));
    }

    /**
     *
     * @param key {@link K key}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}.
     * @return {@link OptionalValue}
     * @param <C> type tp cast to
     * @param <R> type your converter converts to
     * @param <E> {@link Exception} your converter may throw
     * @throws E by your converter
     * @throws ClassCastException if the value for given key is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C, R, E extends Exception> @NotNull OptionalValue<R> getOptionalValueAndConvertWithException(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return OptionalValue.of();
        return OptionalValue.of(converter.convert((C) entry.getValue()));
    }

    default @NotNull Container<V> getContainer(@NotNull K key) {
        Entry<K, V> entry = getEntry(key);
        boolean exists = entry != null;
        return new BasicContainer<>(key, exists, exists ? entry.getValue() : null);
    }

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
     * @param key {@link K key}
     * @param defaultObject default object to return if no entry with given key exists
     * @param defaultObjectIfNull default object to return if the value of the entry with given key is {@code null}
     * @return value (cast to {@link C}) of entry with given key or {@code null} if the value of given key is {@code null} or given defaultObject is {@code null} and
     * there is no entry for given key.
     * @param <C> class to which the value of the entry should be cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     */
    @Contract("_, !null, !null -> !null")
    @SuppressWarnings("unchecked")
    default <C extends V> @Nullable C getAs(@NotNull K key, @Nullable C defaultObject, @Nullable C defaultObjectIfNull) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        V value = entry.getValue();
        return value == null ? defaultObjectIfNull : (C) value;
    }

    /**
     *
     * @param key {@link K key}
     * @param defaultObject default object to return if no entry with given key exists
     * @return value (cast to {@link C}) of entry with given key or {@code null} if the value of given key is {@code null} or given defaultObject is {@code null} and
     * there is no entry for given key.
     * @param <C> class to which the value of the entry should be cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    default <C extends V> @Nullable C getAs(@NotNull K key, @Nullable C defaultObject) {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        return (C) entry.getValue();
    }

    /**
     *
     * @param key {@link K key}
     * @return value (cast to {@link C}) of entry with given key or {@code null} if the value of the entry is {@code null} or if no such entry exits
     * @param <C> class to which the value of the entry should be cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @see #getAs(Object, Object)
     */
    default <C extends V> @Nullable C getAs(@NotNull K key) {
        return getAs(key, null);
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
     * @return result {@link R} or {@code null} if your functions returns {@code null}
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
     * @param defaultObject object to return if {@link #get(Object)} with given key is {@code null}. (if entry or value is {@code null})
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(Object)} with given is {@code null} or if your functions returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     * @see #getAndConvertOrDefault(Object, Converter, Object, Object) 
     * @see #getAndConvertOrDefaultBoth(Object, Converter, Object)
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
     * @param key {@link K key}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}#
     * @param defaultObject default object to return if no entry with given key exists
     * @param defaultObjectIfNull default object to return if the value of the entry with given key is {@code null}
     * @return result {@link R} or {@code null} if the value of the entry is {@code null} or if no entry with given key exists and your default object is {@code null}
     * @param <C> the convertible type
     * @param <R> the result type
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    @Contract("_, _, !null, !null -> !null")
    @Nullable
    default <C extends V, R> R getAndConvertOrDefault(@NotNull K key, @NotNull Converter<C, R> converter, @Nullable R defaultObject, @Nullable R defaultObjectIfNull){
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return defaultObject;
        return entry.getValue() == null ? defaultObjectIfNull : converter.convert((C) entry.getValue());
    }

    /**
     *
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param defaultObject object to return if {@link #get(Object)} with given key is {@code null}. (if entry or value is {@code null})
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(Object)} with given is {@code null} or if your functions returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     */
    @Nullable
    @Contract("_, _, !null -> !null")
    default <C extends V, R> R getAndConvertOrDefaultBoth(@NotNull K key, @NotNull Converter<C, R> converter, @Nullable R defaultObject){
        return getAndConvert(key, converter, defaultObject);
    }

    /**
     * The value returned by {@link #get(Object)} with given key must be of type {@link C} or {@code null}.<br>
     * If the value returned by {@link #get(Object)} with given key is {@code null},
     * {@link ExceptionConverter#convert(Object)} with {@code null}.
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param <C> the convertible type
     * @param <R> the result type
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return result {@link R} or {@code null} if your functions returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}
     * @throws E if {@link ExceptionConverter#convert(Object)} throws an Exception
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default  <C extends V, R, E extends Exception> R getAndConvertWithException(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
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
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(Object)} with given is {@code null} or if your functions returns {@code null}
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
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create a byte or throw
     *                     a factory
     * @return {@link Number#byteValue() byte value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> byte getNumberAsByte(@NotNull K key, @NotNull ValueFactory<K, Byte, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.byteValue();
    }

    /**
     *
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create a short or throw
     *                     a factory
     * @return {@link Number#shortValue() short value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> short getNumberAsShort(@NotNull K key, @NotNull ValueFactory<K, Short, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.shortValue();
    }

    /**
     *
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create an int or throw
     *                     a factory
     * @return {@link Number#intValue() int value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> int getNumberAsInt(@NotNull K key, @NotNull ValueFactory<K, Integer, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.intValue();
    }

    /**
     *
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create an int or throw
     *                     a factory
     * @return {@link Number#longValue() long value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> long getNumberAsLong(@NotNull K key, @NotNull ValueFactory<K, Long, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.longValue();
    }

    /**
     *
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create a float or throw
     *                     a factory
     * @return {@link Number#floatValue() float value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> float getNumberAsFloat(@NotNull K key, @NotNull ValueFactory<K, Float, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.floatValue();
    }

    /**
     *
     * @param key the key for the entry which should be of type {@link Number}
     * @param valueFactory if {@link #get(Object)} returns {@code null}, this factory is used to create a double or throw
     *                     a factory
     * @return {@link Number#doubleValue() double value} of {@link Number} value at given key
     * or created value by your valueFactory if {@link #get(Object)} returned {@code null}.
     * @param <E> factory that may be thrown by your {@link ValueFactory}
     * @throws E may be thrown by your {@link ValueFactory}
     */
    default <E extends Exception> double getNumberAsDouble(@NotNull K key, @NotNull ValueFactory<K, Double, E> valueFactory) throws E {
        Number number = (Number) get(key);
        if(number == null) return valueFactory.create(key);
        return number.doubleValue();
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
     * @param converter to convert from {@link C value contained in the implementations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implementations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implementations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
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
     * @param converter to convert from {@link C value contained in the implementations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implementations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implementations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
     * @throws E if the functions throws this exception
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
     *     Each element from the implementations - returned by {@link #getList(Object)} with given key - will be set to {@code null},
     *     after it has been converted and stored in the implementations that will be returned. After every element of the former implementations has been converted,
     *     {@link List#clear()} will be called.
     * </p>
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @param converter to convert from {@link C value contained in the implementations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implementations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implementations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
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
     *     Each element from the implementations - returned by {@link #getList(Object)} with given key - will be set to {@code null},
     *     after it has been converted and stored in the implementations that will be returned. After every element of the former implementations has been converted,
     *     {@link List#clear()} will be called.
     * </p>
     *
     * @param key the key for the entry, whose value is of type {@link List} of {@link Object}
     * @param converter to convert from {@link C value contained in the implementations} to the {@link R result type}
     * @return {@link ArrayList} of {@link R} or {@code null} if {@link #getList(Object)} with given key returns {@code null}
     * @param <C> type which all elements of the implementations returned by {@link #getList(Object)} will be cast to
     * @param <R> result type
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link List} of {@link Object}
     * @throws ClassCastException if the elements of the implementations returned by {@link #getList(Object)} with given key cannot be cast to {@link C}
     * @throws E if given converter throws an exception ({@link ExceptionConverter#convert(Object)})
     */
    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @Nullable ArrayList<R> getListAndConvertWithExceptionAndFreeMemory(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
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
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key for the entry
     * @param consumer consumer to process the entry, if it exists
     * @param <C> type to cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @return {@code true} if an entry with given key exists, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    default <C extends V> boolean processIfContained(@NotNull K key, @NotNull Consumer<C> consumer){
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            consumer.accept((C) entry.getValue());
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     If an entry with given key exists, it will be {@link Converter#convert(Object) converted} by given {@code converter},
     *     then processed by given {@code consumer} and {@code true} will be returned.<br>
     *     If an entry with given key does not exists, {@code false} will be returned.<br>
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key {@link K} for the entry.
     * @param converter to convert from {@link C} to {@link R}.
     * @param consumer consumer to process the entry, if it exists.
     * @param <C> type to cast to.
     * @param <R> type to convert to.
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise.
     */
    @SuppressWarnings("unchecked")
    default <C extends V, R> boolean convertAndProcessIfContained(@NotNull K key, @NotNull Converter<C, R> converter, @NotNull Consumer<R> consumer){
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            consumer.accept(converter.convert((C) entry.getValue()));
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     If an entry with given key exists, it will be {@link ExceptionConverter#convert(Object) converted} by given {@code converter},
     *     then processed by given {@code consumer} and {@code true} will be returned.<br>
     *     If an entry with given key does not exists, {@code false} will be returned.<br>
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key {@link K} for the entry.
     * @param converter to convert from {@link C} to {@link R}.
     * @param consumer consumer to process the entry, if it exists.
     * @param <C> type to cast to.
     * @param <R> type to convert to.
     * @param <E> exception your converter can throw.
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}.
     * @throws E if your {@link ExceptionConverter} throws this exception.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise.
     */
    @SuppressWarnings("unchecked")
    default <C extends V, R, E extends Throwable> boolean convertWithExceptionAndProcessIfContained(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter, @NotNull Consumer<R> consumer) throws E {
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            consumer.accept(converter.convert((C) entry.getValue()));
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     If an entry with key exists, it will be cast to a {@link List} and then processed by given
     *     {@code consumer} and {@code true} will be returned.
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key {@link K} for the entry.
     * @param consumer consumer to process the entry, if it exists.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise.
     * @throws ClassCastException if the value of the entry with given key is not a {@link List}.
     */
    @SuppressWarnings("unchecked")
    default boolean processListIfContained(@NotNull K key, @NotNull Consumer<List<Object>> consumer) {
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            consumer.accept((List<Object>) entry.getValue());
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     If an entry with given {@code key} exists, it will be cast to a {@link List} and every element cast to {@link C}
     *     {@link Converter#convert(Object) converted} to {@link R}. All elements will be added to a new {@link List},
     *     which will then be processed by given {@code consumer} and {@code true} will be returned.
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key {@link K} for the entry.
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param consumer consumer to process the entry, if it exists.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise.
     * @throws ClassCastException if the value of the entry with given key is not a {@link List}
     * or if any element inside the list is not of type {@link C}.
     */
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    default <C, R> boolean convertAndProcessListIfContained(@NotNull K key, @NotNull Converter<C, R> converter, @NotNull Consumer<List<R>> consumer) {
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            List<Object> list = (List<Object>) entry.getValue();

            if(list == null) {
                consumer.accept(null);
                return true;
            }

            ArrayList<R> converted = new ArrayList<>(list.size());
            for(Object o : list)
                converted.add(converter.convert((C) o));
            consumer.accept(converted);
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     If an entry with given {@code key} exists, it will be cast to a {@link List} and every element cast to {@link C}
     *     {@link ExceptionConverter#convert(Object) converted} to {@link R}. All elements will be added to a new {@link List},
     *     which will then be processed by given {@code consumer} and {@code true} will be returned.
     * </p>
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * The entry's value may still be {@code null}. To assure non-null values use {@link #processIfNotNull(Object, Consumer)}.
     * @param key the key {@link K} for the entry.
     * @param converter {@link ExceptionConverter} to convert from {@link C} to {@link R}
     * @param consumer consumer to process the entry, if it exists.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise.
     * @throws ClassCastException if the value of the entry with given key is not a {@link List}
     * or if any element inside the list is not of type {@link C}.
     */
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    default <C, R, E extends Throwable> boolean convertWithExceptionAndProcessListIfContained(@NotNull K key, @NotNull ExceptionConverter<C, R, E> converter, @NotNull Consumer<List<R>> consumer) throws E {
        Entry<K, V> entry = getEntry(key);
        if(entry != null){
            List<Object> list = (List<Object>) entry.getValue();

            if(list == null) {
                consumer.accept(null);
                return true;
            }

            ArrayList<R> converted = new ArrayList<>(list.size());
            for(Object o : list)
                converted.add(converter.convert((C) o));
            consumer.accept(converted);
            return true;
        }
        return false;
    }

    /**
     * <ul>
     *     <li>
     *         If an entry with given key exists and it's value is not {@code null}, it will be processed by given
     *         consumer and {@code true} will be returned.
     *     </li>
     *     <li>
     *         If an entry with given key exists and it's value is {@code null}, the exception {@link E} supplied
     *         by your {@link ExceptionSupplier} will be thrown.
     *     </li>
     *     <li>
     *         If an entry with given key does not exists, {@code false} will be returned.
     *     </li>
     * </ul>
     *
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * if {@link #get(Object)} returns {@code null}, {@link Converter#convert(Object)} will <b>not</b> be called. So
     * it is safe for your {@link Converter} to assume not-null values.
     *
     * @param key the key for the entry
     * @param consumer consumer to process the entry, if it exists
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @param <C> type to cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @throws E if an entry with given key exists, and its value is {@code null}.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise
     */
    @SuppressWarnings("unchecked")
    default <C extends V, E extends Throwable> boolean processIfContainedAndRequireNotNull(@NotNull K key, @NotNull Consumer<C> consumer, ExceptionSupplier<K,AbstractData<K, V>, E> exceptionSupplier) throws E {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return false;
        if(entry.getValue() == null) throw exceptionSupplier.supply(this, key);
        consumer.accept((C) entry.getValue());
        return true;
    }

    /**
     * <ul>
     *     <li>
     *         If an entry with given key exists and it's value is not {@code null}, it will be cast to {@link C},
     *         converted to {@link R} and then
     *         processed by given
     *         consumer and {@code true} will be returned.
     *     </li>
     *     <li>
     *         If an entry with given key exists and it's value is {@code null}, the exception {@link E} supplied
     *         by your {@link ExceptionSupplier} will be thrown.
     *     </li>
     *     <li>
     *         If an entry with given key does not exists, {@code false} will be returned.
     *     </li>
     * </ul>
     *
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * if {@link #get(Object)} returns {@code null}, {@link Converter#convert(Object)} will <b>not</b> be called. So
     * it is safe for your {@link Converter} to assume not-null values.
     *
     * @param key the key for the entry
     * @param converter {@link Converter} to convert from {@link C} to {@link R}.
     * @param consumer consumer to process the entry, if it exists
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @param <C> type to cast to
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @throws E if an entry with given key exists, and its value is {@code null}.
     * @return {@code true} if an entry with given key exists, {@code false} otherwise
     */
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    default <C extends V, R, E extends Throwable> boolean convertAndProcessIfContainedAndRequireNotNull(@NotNull K key, Converter<C, R> converter, @NotNull Consumer<R> consumer, ExceptionSupplier<K,AbstractData<K, V>, E> exceptionSupplier) throws E {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return false;
        if(entry.getValue() == null) throw exceptionSupplier.supply(this, key);
        consumer.accept(converter.convert((C) entry.getValue()));
        return true;
    }

    /**
     * <ul>
     *     <li>
     *         If an entry with given key exists and it's value is not {@code null}, it will be cast to {@link C},
     *         converted to {@link R} and then
     *         processed by given
     *         consumer and {@code true} will be returned.
     *     </li>
     *     <li>
     *         If an entry with given key exists and it's value is {@code null}, the exception {@link E} supplied
     *         by your {@link ExceptionSupplier} will be thrown.
     *     </li>
     *     <li>
     *         If an entry with given key does not exists, {@code false} will be returned.
     *     </li>
     * </ul>
     *
     * @implNote The entry will <b>NOT</b> be removed from this {@link AbstractData}. <br>
     * if {@link #get(Object)} returns {@code null}, {@link Converter#convert(Object)} will <b>not</b> be called. So
     * it is safe for your {@link Converter} to assume not-null values.
     *
     * @param key the key for the entry
     * @param converter {@link ExceptionConverter} to convert from {@link C} to {@link R}.
     * @param consumer consumer to process the entry, if it exists
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @param <C> type to cast to.
     * @param <E> exception supplied by your {@link ExceptionSupplier}.
     * @param <R> type to convert to.
     * @param <F> exception thrown by your {@link ExceptionConverter}.
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @throws E if an entry with given key exists, and its value is {@code null}.
     * @throws F if your {@code converter} throws this exception
     * @return {@code true} if an entry with given key exists, {@code false} otherwise
     */
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    default <C extends V, R, E extends Throwable, F extends Throwable> boolean convertWithExceptionAndProcessIfContainedAndRequireNotNull(@NotNull K key, ExceptionConverter<C, R, F> converter, @NotNull Consumer<R> consumer, ExceptionSupplier<K,AbstractData<K, V>, E> exceptionSupplier) throws E, F {
        Entry<K, V> entry = getEntry(key);
        if(entry == null) return false;
        if(entry.getValue() == null) throw exceptionSupplier.supply(this, key);
        consumer.accept(converter.convert((C) entry.getValue()));
        return true;
    }

    /**
     *
     * @param key the key for the entry
     * @param consumer consumer to process the entry, if it exists, and it's value is not {@code null}.
     * @return {@code true} if an entry with given key exists, and it's value is not {@code null}. {@code false} otherwise
     * @throws ClassCastException if the value of the entry with given key is not of type {@link C}
     * @param <C> type to cast to
     */
    @SuppressWarnings("unchecked")
    default <C extends V> boolean processIfNotNull(@NotNull K key, @NotNull Consumer<C> consumer) {
        Entry<K, V> entry = getEntry(key);
        if(entry != null && entry.getValue() != null){
            consumer.accept((C) entry.getValue());
            return true;
        }
        return false;
    }

    /**
     * <p>
     *     {@link #get(Object) Gets} the value with given key and returns it, if the value is not {@code null}.
     *     If the value is {@code null}, {@link E} supplied by your {@link ExceptionSupplier} will be thrown.
     * </p>
     * @param key {@link V} key
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @return {@link V value} returned by {@link #get(Object)}. Never {@code null}.
     * @param <E> The {@link Throwable} to be thrown if {@link #get(Object)} with given key returns {@code null}.
     * @throws E if {@link #get(Object)} with given key returns {@code null}.
     */
    @NotNull
    default <E extends Throwable> Object getAndRequireNotNull(@NotNull K key, @NotNull ExceptionSupplier<K, AbstractData<K, V>, E> exceptionSupplier) throws E {
        Object obj = get(key);
        if(obj == null) throw exceptionSupplier.supply(this, key);
        return obj;
    }

    /**
     * <p>
     *     {@link #get(Object) Gets} the value with given key, casts it to {@link C} and returns it, if the value is not {@code null}.
     *     If the value is {@code null}, {@link E} supplied by your {@link ExceptionSupplier} will be thrown.
     * </p>
     * @param key {@link V} key
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @return {@link V value} cast to {@link C} returned by {@link #get(Object)}. Never {@code null}.
     * @param <C> The class to cast to.
     * @param <E> The {@link Throwable} to be thrown if {@link #get(Object)} with given key returns {@code null}.
     * @throws E if {@link #get(Object)} with given key returns {@code null}.
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default <C extends V, E extends Throwable> C getAsAndRequireNotNull(@NotNull K key, @NotNull ExceptionSupplier<K, AbstractData<K, V>, E> exceptionSupplier) throws E {
        C obj = (C) get(key);
        if(obj == null) throw exceptionSupplier.supply(this, key);
        return obj;
    }

    /**
     * <p>
     *     {@link #get(Object) Gets} the value with given key and casts it to {@link C}.
     *     If the value is not {@code null} it will be {@link Converter#convert(Object) converted} to {@link R} and then returned.
     *     If the value is {@code null}, {@link E} supplied by your {@link ExceptionSupplier} will be thrown.
     * </p>
     * <p>
     *     Note: if {@link #get(Object)} returns {@code null}, {@link Converter#convert(Object)} will <b>not</b> be called. So
     *     it is safe for your {@link Converter} to assume not-null values.
     * </p>
     * @param key {@link V} key
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @param converter {@link Converter} to convert from {@link C} to {@link R}.
     * @return {@link V value} cast to {@link C} returned by {@link #get(Object)}. Never {@code null}.
     * @param <C> The class to cast to.
     * @param <E> The {@link Throwable} to be thrown if {@link #get(Object)} with given key returns {@code null}.
     * @param <R> The class to {@link Converter#convert(Object) convert} to.
     * @throws E if {@link #get(Object)} with given key returns {@code null}.
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default <C extends V, R, E extends Throwable> R getAndRequireNotNullAndConvert(@NotNull K key, @NotNull Converter<C, R> converter, @NotNull ExceptionSupplier<K, AbstractData<K, V>, E> exceptionSupplier) throws E {
        C obj = (C) get(key);
        if(obj == null) throw exceptionSupplier.supply(this, key);
        return converter.convert(obj);
    }

    /**
     * <p>
     *     {@link #get(Object) Gets} the value with given key and casts it to {@link C}.
     *     If the value is not {@code null} it will be {@link ExceptionConverter#convert(Object) converted} to {@link R} and then returned.
     *     If the value is {@code null}, {@link E} supplied by your {@link ExceptionSupplier} will be thrown.
     * </p>
     * @implNote if {@link #get(Object)} returns {@code null}, {@link ExceptionConverter#convert(Object)} will <b>not</b> be called. So
     * it is safe for your {@link ExceptionConverter} to assume not-null values.
     * @param key {@link V} key
     * @param exceptionSupplier {@link ExceptionSupplier} supplies a {@link Throwable} if {@link #get(Object)} with given key returns {@code null}.
     * @param converter {@link ExceptionConverter} to convert from {@link C} to {@link R}.
     * @return {@link V value} cast to {@link C} returned by {@link #get(Object)}. Never {@code null}.
     * @param <C> The class to cast to.
     * @param <E> The {@link Throwable} to be thrown if {@link #get(Object)} with given key returns {@code null}.
     * @param <F> The {@link Throwable} which your {@link ExceptionConverter} can throw.
     * @param <R> The class to {@link Converter#convert(Object) convert} to.
     * @throws E if {@link #get(Object)} with given key returns {@code null}.
     * @throws ClassCastException if the value returned by {@link #get(Object)} with given key is not of type {@link C}.
     * @throws F if your {@link ExceptionConverter} throws this exception.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    default <C extends V, R, E extends Throwable, F extends Throwable> R getAndRequireNotNullAndConvertWithException(@NotNull K key, @NotNull ExceptionConverter<C, R, F> converter, @NotNull ExceptionSupplier<K, AbstractData<K, V>, E> exceptionSupplier) throws E, F {
        C obj = (C) get(key);
        if(obj == null) throw exceptionSupplier.supply(this, key);
        return converter.convert(obj);
    }

    /**
     * Converts this {@link AbstractData} to given {@code recordClass}. This conversion supports all
     * primitive types, {@link String} and records.
     * @param recordClass the class to the record {@link T}.
     * @return record {@link T} filled by this {@link AbstractData}.
     * @param <T> record class
     */
    default <T extends Record> @NotNull T toRecord(Class<T> recordClass) {
        if(!recordClass.isRecord())
            throw new IllegalArgumentException("Given recordClass '" + recordClass.getCanonicalName() + "' is not a record.");

        RecordComponent[] comps = recordClass.getRecordComponents();
        Object[] params = new Object[comps.length];

        loop: for (var entry : this) {
            for (int i = 0; i < comps.length; i++) {
                if (comps[i].getName().equals(entry.getKey().toString())) {
                    if(comps[i].getType().isRecord() && entry.getValue() != null) {
                        //noinspection unchecked
                        params[i] = ((AbstractData<?,?>) entry.getValue()).toRecord((Class<? extends Record>) comps[i].getType());
                    } else if (entry.getValue() instanceof Number num) {
                        params[i] = NumberUtils.convertTo(num, comps[i].getType());
                    } else {
                        params[i] = entry.getValue();
                    }
                    continue loop;
                }
            }

        }

        Class<?>[] componentTypes = Arrays.stream(comps).map(RecordComponent::getType).toArray(Class<?>[]::new);
        try {
            return recordClass.getDeclaredConstructor(componentTypes).newInstance(params);
        } catch (NoSuchMethodException | InstantiationException e) {
            // Should never happen
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If a value with given {@code key} exists ({@link #getEntry(Object)} is not null), it will be returned.
     * Otherwise, the value returned by given {@code supplier} will be added at given {@code key}
     * and returned.
     */
    default V computeIfAbsent(@NotNull K key, @NotNull Function<K, V> supplier) {
        var entry = getEntry(key);
        if(entry != null)
            return entry.getValue();
        V value = supplier.apply(key);
        add(key, value);
        return value;
    }

    /**
     * If a value with given {@code key} is not null ({@link #get(Object)} is not null), it will be returned.
     * Otherwise, the value returned by given {@code supplier} will be added at given {@code key}
     * and returned.
     */
    default V computeIfAbsentOrNull(@NotNull K key, @NotNull Function<K, V> supplier) {
        var entry = getEntry(key);
        if(entry != null && entry.getValue() != null)
            return entry.getValue();

        V value = supplier.apply(key);

        if(entry != null) entry.setValue(value);
        else add(key, value);

        return value;
    }

    /**
     * {@link #add(Object, Object) Adds} given {@code value} at given {@code key} and
     * returns given {@code value}.
     */
    default <T extends V> T addAndGet(@NotNull K key, T value) {
        add(key, value);
        return value;
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

    /**
     * Removes all key value pairs from this {@link AbstractData}.
     */
    void clear();

    /**
     * Writes this {@link AbstractData} to a {@link StringBuilder}.
     * @return {@link StringBuilder}
     */
    default @NotNull StringBuilder toJsonString() {
        return PARSER.writeDataToStringBuilder(this);
    }

    /**
     * How this {@link AbstractData} should be parsed
     * @return {@link ParseType}
     */
    @ApiStatus.Internal
    default @NotNull ParseType getParseType() {
        return ParseType.NORMAL;
    }

    @Override
    default AbstractData<K, V> getData() {
        return this;
    }
}
