/*
 * Copyright (c) 2023 Linus Andera
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

package de.linusdev.data.container;

import de.linusdev.data.functions.ExceptionConverter;
import de.linusdev.data.AbstractData;
import de.linusdev.data.functions.Converter;
import de.linusdev.data.functions.ExceptionSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NonExistentContainer<K, V, O> implements Container<K, V, O> {

    private final @NotNull AbstractData<K, V> parentData;
    private final @NotNull K key;

    public NonExistentContainer(@NotNull AbstractData<K, V> parentData, @NotNull K key) {
        this.parentData = parentData;
        this.key = key;
    }

    @Override
    public O get() {
        throw new NonExistentException(getKey());
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public @NotNull K getKey() {
        return key;
    }

    @Override
    public @NotNull AbstractData<K, V> getParentData() {
        return parentData;
    }

    @Override
    public @NotNull <N> Container<K, V, N> createNewContainer(@Nullable N newValue) {
        return new NonExistentContainer<>(parentData, key);
    }

    @Override
    public @NotNull <T> ListContainer<T> createNewListContainer(@Nullable List<T> newValue) {
        return new NonExistentListContainer<>();
    }

    @Override
    public @NotNull <E extends Throwable> Container<K, V, O> requireNotNull(ExceptionSupplier<K, AbstractData<K, V>, E> supplier) throws E {
        throw supplier.supply(parentData, key);
    }

    @Override
    public @NotNull Container<K, V, O> requireNotNull() throws NullPointerException {
        return this;
    }

    @Override
    public @NotNull ListContainer<Object> asList() {
        return createNewListContainer(null);
    }

    @Override
    public @NotNull <C, R> Container<K, V, R> castAndConvert(@NotNull Converter<C, R> converter) {
        return createNewContainer(null);
    }

    @Override
    public @NotNull <C> Container<K, V, C> cast() {
        return createNewContainer(null);
    }

    @Override
    public @NotNull <C, R, E extends Throwable> Container<K, V, R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) {
        return createNewContainer(null);
    }

    @Override
    public @NotNull Container<K, V, O> ifExists() {
        return this;
    }

    @Override
    public @NotNull Container<K, V, O> process(@NotNull Consumer<O> consumer) {
        return this;
    }
}
