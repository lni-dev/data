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

import me.linusdev.data.functions.Converter;
import me.linusdev.data.functions.ExceptionConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class NonExistentListContainer<T> implements ListContainer<T>{
    @Override
    public List<T> get() {
        return null;
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
    public @NotNull <N> ListContainer<N> createNew(@Nullable List<N> newValue) {
        return new NonExistentListContainer<>();
    }

    @Override
    public @NotNull <C, R> ListContainer<R> castAndConvert(@NotNull Converter<C, R> converter) {
        return new NonExistentListContainer<>();
    }

    @Override
    public @NotNull <C, R, E extends Throwable> ListContainer<R> castAndConvertWithException(@NotNull ExceptionConverter<C, R, E> converter) {
        return new NonExistentListContainer<>();
    }

    @Override
    public @NotNull ListContainer<T> process(@NotNull Consumer<List<T>> consumer) {
        return this;
    }
}
