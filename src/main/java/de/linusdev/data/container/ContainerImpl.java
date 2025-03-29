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

package de.linusdev.data.container;

import de.linusdev.data.AbstractData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ContainerImpl<K, V, O> implements Container<K, V, O> {

    private final @NotNull AbstractData<K, V> parentData;
    private final @NotNull K key;
    private final @Nullable O value;
    private final boolean exists;

    @Contract(pure = true)
    public ContainerImpl(@NotNull AbstractData<K, V> parentData, @NotNull K key, @Nullable O value, boolean exists) {
        this.parentData = parentData;
        this.key = key;
        this.value = value;
        this.exists = exists;
    }

    @Override
    public O get() {
        return value;
    }

    @Override
    public boolean exists() {
        return exists;
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
        return new ContainerImpl<>(parentData, key, newValue, exists);
    }

    @Override
    public @NotNull <T> ListContainer<T> createNewListContainer(@Nullable List<T> newValue) {
        return new ListContainerImpl<>(newValue, exists);
    }
}
