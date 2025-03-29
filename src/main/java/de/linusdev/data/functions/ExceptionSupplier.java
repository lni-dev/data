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

package de.linusdev.data.functions;

import de.linusdev.data.AbstractData;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @param <K> key
 * @param <D> data
 * @param <E> throwable
 */
@FunctionalInterface
public interface ExceptionSupplier <K, D extends AbstractData<K, ?>, E extends Throwable> {

    /**
     * Supplies the throwable {@link E}.
     * @param data {@link AbstractData} that caused the exception.
     * @param key {@link K key} that caused the exception.
     * @return instance of {@link E}.
     */
    @NotNull E supply(@NotNull D data, @NotNull K key);
}
