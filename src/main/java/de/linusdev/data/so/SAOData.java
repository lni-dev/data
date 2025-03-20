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

package de.linusdev.data.so;

import de.linusdev.data.AbstractData;
import de.linusdev.data.implemantations.SAODataListImpl;
import de.linusdev.data.implemantations.SAODataMapImpl;
import de.linusdev.data.implemantations.SODataListImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public interface SAOData<O> extends AbstractData<String, O> {

    /**
     * <p>
     *     This will create a new {@link SAOData} which will keep the order the elements are added.
     *     This {@link SAOData} will be backed by a {@link ArrayList} with given initialCapacity.<br>
     *     This is useful for {@link AbstractData data objects} with known sizes or for {@link AbstractData data objects} with small
     *     sizes.
     * </p>
     * <p>
     *     The {@link #get(Object)} and {@link #remove(Object)} methods of the returned {@link SAOData} will be in O(n), but the
     *     {@link #add(Object, Object)} methods will be in O(1).
     * </p>
     * @param initialCapacity the initialCapacity of the {@link ArrayList}, which will back the {@link SAOData}
     * @return {@link SAOData} backed by a {@link ArrayList}
     */
    @Contract(pure = true)
    static <O> @NotNull SAOData<O> newOrderedDataWithKnownSize(int initialCapacity) {
        return new SAODataListImpl<>(new ArrayList<>(initialCapacity));
    }

    /**
     * <p>
     *     This will create a new {@link SAOData} which will keep the order the elements are added.
     *     This {@link SODataListImpl} will be backed by a backed by a {@link LinkedList}.<br>
     *     This is useful for {@link AbstractData data objects} with Unknown sizes.
     * </p>
     * <p>
     *     The {@link #get(Object)} methods of the returned {@link SAOData} will be in O(n), but the
     *     {@link #add(Object, Object)} and {@link #remove(Object)} methods will be in O(1).
     * </p>
     * @return {@link SAOData} backed by a {@link LinkedList}
     * @see #newOrderedDataWithKnownSize(int) for very small (less than 10 elemets) you should generrally use the
     * ArrayList implemenation
     */
    @Contract(pure = true)
    static <O> @NotNull SAOData<O> newOrderedDataWithUnknownSize() {
        return new SAODataListImpl<>(new LinkedList<>());
    }

    /**
     *
     * @param initialCapacity the initialCapacity of the {@link HashMap} which will back the {@link SAOData}
     * @return {@link SAOData} backed by a {@link HashMap}
     */
    static <O> @NotNull SAOData<O> newHashMapData(int initialCapacity) {
        return new SAODataMapImpl<>(new HashMap<>(initialCapacity));
    }

}
