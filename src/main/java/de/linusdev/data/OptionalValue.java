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

package de.linusdev.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface OptionalValue<V> {

    /**
     * The actual value of class {@link V} represented by this {@link OptionalValue}.
     * @return {@link V} or {@code null}
     */
    V get();

    /**
     * Calls {@link #get()} and casts it {@link C}.
     * @return {@link C} or {@code null}
     * @param <C> class to cast to
     * @throws ClassCastException if {@link V} cannot be cast to {@link C}.
     */
    @SuppressWarnings("unchecked")
    default <C> C getAs() {
        return (C) get();
    }

    /**
     *
     * @return {@code true} if {@link #get()} will return {@code null}.
     */
    default boolean isNull() {
        return get() == null;
    }

    /**
     * If a {@link OptionalValue} exists, the value returned by {@link #get()} can still be {@code null}.
     * @return {@code true} if this {@link OptionalValue} exists in the {@link AbstractData} this {@link OptionalValue} is of.
     */
    @SuppressWarnings("unused")
    boolean exists();

    /**
     *
     * @param value value
     * @return new {@link OptionalValue}, that exists.
     * @param <V> type
     */
    @Contract(value = "_ -> new", pure = true)
    public static <V> @NotNull OptionalValue<V> of(V value) {
        return new OptionalValueImplementation<>(value, true);
    }

    /**
     *
     * @return new {@link OptionalValue}, that does not exist.
     * @param <V> type
     */
    @Contract(value = " -> new", pure = true)
    public static <V> @NotNull OptionalValue<V> of() {
        return new OptionalValueImplementation<>(null, false);
    }

}
