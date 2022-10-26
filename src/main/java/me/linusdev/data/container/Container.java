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

package me.linusdev.data.container;

import me.linusdev.data.AbstractData;
import me.linusdev.data.OptionalValue;
import me.linusdev.data.functions.Converter;
import me.linusdev.data.functions.ExceptionConverter;
import me.linusdev.data.functions.ExceptionSupplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class holds a value from an {@link AbstractData} (called parent data). It
 * offers several methods to process this value. All these methods will not have any effect on the parent data.
 * The value can be accessed using the {@link #get()} method and processed with the {@link #process(Consumer)} method.
 * <br><br>
 * <h2>Code examples</h2>
 * The following code will throw an exception if the json did not contain a key "some_key" or if its value was {@code null}.
 * Otherwise, it will just print the value of "some_key":
 * <pre>{@code
 *  JsonParser parser = new JsonParser();
 *  SOData data = parser.parseReader(new StringReader("{...}"));
 *
 *  data.getContainer("some_key")
 *          .requireNotNull()
 *          .process((Object o) -> System.out.println("some_key: " + o));
 * }</pre>
 * <br>
 * The following code will do nothing if the key "some_value" does not exist. It throws an exception if the key does
 * exist, but its value is {@code null}. And it will print the value of "some_key" if this exists and its value is not
 * {@code null}:
 * <pre>{@code
 *  JsonParser parser = new JsonParser();
 *  SOData data = parser.parseReader(new StringReader("{...}"));
 *
 *  data.getContainer("some_key")
 *          .ifExists()
 *          .requireNotNull()
 *          .process((Object o) -> System.out.println("some_key: " + o));
 * }</pre>
 *
 * @param <K> key type of the {@link #getParentData() parent data}.
 * @param <V> values type of the {@link #getParentData() parent data}.
 * @param <O> type of the contained object.
 */
@SuppressWarnings("unused")
public interface Container<K, V, O> extends OptionalValue<O> {

    /**
     *
     * @return {@link K key} of the original values {@link #getParentData() parent data}.
     */
    @NotNull K getKey();

    /**
     *
     * @return {@link AbstractData parent data}.
     */
    @NotNull AbstractData<K, V> getParentData();

    /**
     * Creates a new {@link Container} with given value.
     * @param newValue the new value
     * @return a new {@link  Container}
     * @param <N> the type of the new value
     */
    @ApiStatus.Internal
    @NotNull <N> Container<K, V, N> createNewContainer(@Nullable N newValue);

    /**
     * Creates a new {@link ListContainer} with given value.
     * @param newValue the list value
     * @return a new {@link ListContainer}
     * @param <T> the list-element type
     */
    @ApiStatus.Internal
    @NotNull <T> ListContainer<T> createNewListContainer(@Nullable List<T> newValue);

    /**
     *
     * @param supplier {@link ExceptionSupplier} to supply an exception of your choice.
     * @return this
     * @param <E> exception thrown if the value is {@code null}.
     * @throws E if the value is {@code null} ({@link #get()} returns {@code null}).
     */
    default <E extends Throwable> @NotNull Container<K, V, O> requireNotNull(ExceptionSupplier<K, AbstractData<K,V>, E> supplier) throws E {
        if(get() == null) throw supplier.supply(getParentData(), getKey());
        return this;
    }

    /**
     *
     * @return this
     * @throws NullPointerException if the value is {@code null} ({@link #get()} returns {@code null}).
     */
    default @NotNull Container<K, V, O> requireNotNull() throws NullPointerException {
        if(get() == null) throw new NullPointerException(getKey() + " is null.");
        return this;
    }

    /**
     * Casts the value to {@link List} of {@link Object Objects} and returns a {@link ListContainer} with this list.
     * @return {@link ListContainer} as specified above.
     * @throws ClassCastException if the value is not a {@link List} of {@link Object Objects}.
     */
    @SuppressWarnings("unchecked")
    default @NotNull ListContainer<Object> asList() {
        return createNewListContainer((List<Object>) get());
    }

    /**
     * Casts the value to {@link C}.
     * @return a new {@link Container} with the new cast value.
     * @param <C> type to cast to.
     * @throws ClassCastException if the value is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C> @NotNull Container<K, V, C> cast() {
        return createNewContainer((C) get());
    }

    /**
     * Casts the value to {@link C} and converts it with given {@code converter}.
     * @param converter the converter to {@link Converter#convert(Object)} from {@link C} to {@link R}.
     * @return a new {@link Container} with the new cast and converted value.
     * @param <C> type to cast to.
     * @param <R> type to {@link Converter#convert(Object)} to.
     * @throws ClassCastException if the value is not of type {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C, R> @NotNull Container<K, V, R> castAndConvert(@NotNull Converter<C, R> converter) {
        return createNewContainer(converter.convert((C) get()));
    }

    /**
     * Casts the value to {@link C} and converts it with given {@code converter}. The converter can throw an exception.
     * @param converter the converter to {@link ExceptionConverter#convert(Object)} from {@link C} to {@link R}.
     * @return a new {@link Container} with the new cast and converted value.
     * @param <C> type to cast to.
     * @param <R> type to {@link Converter#convert(Object)} to.
     * @param <E> exception your converter may throw.
     * @throws E if your converter throws this exception
     */
    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @NotNull Container<K, V, R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) throws E {
        return createNewContainer(converter.convert((C) get()));
    }

    /**
     * If the value does <b>not</b> {@link #exists() exist}, a new {@link NonExistentContainer} will be returned.
     * All Operations on this container will have no effect. This means:
     * <ul>
     *     <li>
     *         {@link #get()} will throw a {@link NonExistentException}.
     *     </li>
     *     <li>
     *         {@link #process(Consumer)} will not execute the given {@link Consumer}.
     *     </li>
     *     <li>
     *         {@link #requireNotNull()} or {@link #requireNotNull(ExceptionSupplier)} will never throw an exception.
     *     </li>
     * </ul>
     * <p>
     *     If the value does {@link #exists() exist}, the {@link Container} itself (this) is returned.
     * </p>
     * <p>
     *     This method should be mainly used in combination with the {@link #process(Consumer)} method and then functions
     *     similar to {@link AbstractData#processIfContained(Object, Consumer)}, but can be combined with all other
     *     container methods.
     * </p>
     * @return {@link Container}
     */
    default @NotNull Container<K, V, O> ifExists() {
        if(exists()) return this;
        return new NonExistentContainer<>(getParentData(), getKey());
    }

    /**
     * This method is especially useful in combination with {@link #ifExists()} and {@link #requireNotNull()}.
     * @param consumer {@link Consumer} to process the value.
     * @return this
     */
    @SuppressWarnings("UnusedReturnValue")
    default @NotNull Container<K, V, O> process(@NotNull Consumer<O> consumer) {
        consumer.accept(get());
        return this;
    }
}
