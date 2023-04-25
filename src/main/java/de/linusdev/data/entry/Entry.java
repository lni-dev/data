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

package de.linusdev.data.entry;

import de.linusdev.data.AbstractData;
import de.linusdev.data.implemantations.SODataMapImpl;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A {@link Entry} represents a key-value pair in a {@link AbstractData}.
 * Every {@link Entry} may only be contained in one {@link AbstractData} at a time
 * (This is especially true for {@link SODataMapImpl}).
 * @param <K> key
 * @param <V> value
 */
public interface Entry<K, V> {

    /**
     *
     * @return the {@link V value} of this entry
     */
    V getValue();

    /**
     * sets the {@link V value} of this entry
     * @param value the new value for this entry
     */
    void setValue(V value);

    /**
     * <b>Internal</b><br>
     * Used by some {@link AbstractData} implementations to assure sync between the {@link Entry} and the {@link AbstractData}
     * @param getter the new getter
     */
    void overwriteGetValue(@Nullable Function<Entry<K, V>, V> getter);

    /**
     * <b>Internal</b><br>
     * Used by some {@link AbstractData} implementations to assure sync between the {@link Entry} and the {@link AbstractData}
     * @param setter the new setter
     */
    void overwriteSetValue(@Nullable BiConsumer<Entry<K, V>, V> setter);

    /**
     *
     * @return the {@link K key} of this entry
     */
    K getKey();
}
