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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ListContainerImpl<T> implements ListContainer<T> {

    private final @Nullable List<T> list;
    private final boolean exists;

    public ListContainerImpl(@Nullable List<T> list, boolean exists) {
        this.list = list;
        this.exists = exists;
    }


    @Override
    public List<T> get() {
        return list;
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public @NotNull <N> ListContainer<N> createNew(@Nullable List<N> newValue) {
        return new ListContainerImpl<>(newValue, exists);
    }
}
