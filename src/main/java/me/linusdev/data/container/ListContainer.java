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

import me.linusdev.data.OptionalValue;
import me.linusdev.data.functions.Converter;
import me.linusdev.data.functions.ExceptionConverter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface ListContainer<T> extends OptionalValue<List<T>> {

    @ApiStatus.Internal
    <N> @NotNull ListContainer<N> createNew(@Nullable List<N> newValue);

    @SuppressWarnings("unchecked")
    default <C> @NotNull ListContainer<C> cast() {
        if(isNull()) return createNew(null);

        List<T> list = get();
        ArrayList<C> converted = new ArrayList<>(list.size());
        for(T t : list) converted.add((C) t);

        return createNew(converted);
    }

    default <C, R> @NotNull ListContainer<R> castAndConvert(@NotNull Converter<C, R> converter) {
        return castAndConvertWithException(converter);
    }

    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @NotNull ListContainer<R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) throws E {
        if(isNull()) return createNew(null);

        List<T> list = get();
        ArrayList<R> converted = new ArrayList<>(list.size());
        for(T t : list) converted.add(converter.convert((C) t));

        return createNew(converted);
    }

    default @NotNull ListContainer<T> process(@NotNull Consumer<List<T>> consumer) {
        consumer.accept(get());
        return this;
    }
}
