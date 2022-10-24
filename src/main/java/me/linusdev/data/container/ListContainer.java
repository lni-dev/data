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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class holds a {@link List} value from an {@link AbstractData}. It offers
 * several methods to process the elements of this list and functions similar to it's
 * single value counterpart: {@link Container}.
 * @param <T>
 */
@SuppressWarnings("unused")
public interface ListContainer<T> extends OptionalValue<List<T>> {

    /**
     * Creates a new {@link ListContainer container} with given value.
     * @param newValue the value for the new container
     * @return a new {@link ListContainer}.
     * @param <N> the new value type.
     */
    @ApiStatus.Internal
    <N> @NotNull ListContainer<N> createNew(@Nullable List<N> newValue);

    /**
     * Casts each element of the list to {@link C}.
     * @return a new {@link ListContainer} containing the {@link List} of {@link C}.
     * @param <C> the type to cast to.
     * @throws ClassCastException if any element inside this container's list cannot be cast to {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C> @NotNull ListContainer<C> cast() {
        if(isNull()) return createNew(null);

        List<T> list = get();
        ArrayList<C> converted = new ArrayList<>(list.size());
        for(T t : list) converted.add((C) t);

        return createNew(converted);
    }

    /**
     * Casts each element of the list to {@link C} and then converts it with given converter.
     * @param converter {@link Container} to convert from {@link C} to {@link R}.
     * @return a new {@link ListContainer} containing the {@link List} of {@link R}.
     * @param <C> type to cast to.
     * @param <R> type to convert to.
     * @throws ClassCastException if any element inside this container's list cannot be cast to {@link C},
     */
    default <C, R> @NotNull ListContainer<R> castAndConvert(@NotNull Converter<C, R> converter) {
        return castAndConvertWithException(converter);
    }

    /**
     * Casts each element of the list to {@link C} and then converts it with given converter.
     * @param converter {@link Container} to convert from {@link C} to {@link R}.
     * @return a new {@link ListContainer} containing the {@link List} of {@link R}.
     * @param <C> type to cast to.
     * @param <R> type to convert to.
     * @param <E> exception type of your {@link ExceptionConverter}.
     * @throws ClassCastException if any element inside this container's list cannot be cast to {@link C}.
     * @throws E if your converter throws this exception.
     */
    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @NotNull ListContainer<R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) throws E {
        if(isNull()) return createNew(null);

        List<T> list = get();
        ArrayList<R> converted = new ArrayList<>(list.size());
        for(T t : list) converted.add(converter.convert((C) t));

        return createNew(converted);
    }

    /**
     * Process this container's list with given {@link Consumer}.
     * @param consumer to consume this list.
     * @return this
     */
    default @NotNull ListContainer<T> process(@NotNull Consumer<List<T>> consumer) {
        consumer.accept(get());
        return this;
    }
}
