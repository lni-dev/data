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

@SuppressWarnings("unused")
public interface Container<K, V, O> extends OptionalValue<O> {

    @NotNull K getKey();

    @NotNull AbstractData<K, V> getParentData();

    @ApiStatus.Internal
    @NotNull <N> Container<K, V, N> createNewContainer(@Nullable N newValue);

    @ApiStatus.Internal
    @NotNull <T> ListContainer<T> createNewListContainer(@Nullable List<T> newValue);

    default <E extends Throwable> @NotNull Container<K, V, O> requireNotNull(ExceptionSupplier<K, AbstractData<K,V>, E> supplier) throws E {
        if(get() == null) throw supplier.supply(getParentData(), getKey());
        return this;
    }

    default @NotNull Container<K, V, O> requireNotNull() throws NullPointerException {
        if(get() == null) throw new NullPointerException(getKey() + " is null.");
        return this;
    }

    @SuppressWarnings("unchecked")
    default @NotNull ListContainer<Object> asList() {
        return createNewListContainer((List<Object>) get());
    }

    @SuppressWarnings("unchecked")
    default <C, R> @NotNull Container<K, V, R> castAndConvert(@NotNull Converter<C, R> converter) {
        return createNewContainer(converter.convert((C) get()));
    }

    @SuppressWarnings("unchecked")
    default <C, R, E extends Throwable> @NotNull Container<K, V, R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) throws E {
        return createNewContainer(converter.convert((C) get()));
    }

    default @NotNull Container<K, V, O> ifExists() {
        if(exists()) return this;
        return new NonExistentContainer<>(getParentData(), getKey());
    }

    @SuppressWarnings("UnusedReturnValue")
    default @NotNull Container<K, V, O> process(@NotNull Consumer<O> consumer) {
        consumer.accept(get());
        return this;
    }

}
