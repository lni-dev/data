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

package me.linusdev.data.so;

import me.linusdev.data.AbstractData;
import me.linusdev.data.implemantations.DataListImpl;
import me.linusdev.data.implemantations.DataMapImpl;
import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This represents a {@link AbstractData}, but the keys are {@link String strings} and the values are {@link Object objects}.
 * <br><br>
 * {@link SOData} can be parsed to a json and json can be parsed to {@link SOData} using {@link JsonParser}.
 * See {@link JsonParser} for restrictions.
 * <br><br>
 * There are different {@link SOData} implementations:
 * <ul>
 *     <li>{@link DataListImpl}</li>
 *     <li>{@link DataMapImpl}</li>
 * </ul>
 * <br>
 * The static methods of this interface should be used to create new {@link SOData} instances:
 * <ul>
 *     <li>{@link #newOrderedDataWithKnownSize(int)}</li>
 *     <li>{@link #newOrderedDataWithUnknownSize()}</li>
 *     <li>{@link #newHashMapData(int)}</li>
 * </ul>
 *
 */
public interface SOData extends AbstractData<String, Object> {

    /**
     * <p>
     *     This will create a new {@link SOData} which will keep the order the elements are added.
     *     This {@link SOData} will be backed by a backed by a {@link ArrayList} with given initialCapacity.<br>
     *     This is useful for {@link AbstractData data objects} with known sizes or for {@link AbstractData data objects} with small
     *     sizes.
     * </p>
     * <p>
     *     The {@link #get(Object)} and {@link #remove(Object)} methods of the returned {@link SOData} will be in O(n), but the
     *     {@link #add(Object, Object)} methods will be in O(1).
     * </p>
     * @param initialCapacity the initialCapacity of the {@link ArrayList}, which will back the {@link SOData}
     * @return {@link SOData} backed by a {@link ArrayList}
     */
    @Contract(pure = true)
    static @NotNull SOData newOrderedDataWithKnownSize(int initialCapacity) {
        return new DataListImpl(new ArrayList<>(initialCapacity));
    }

    /**
     * <p>
     *     This will create a new {@link SOData} which will keep the order the elements are added.
     *     This {@link DataListImpl} will be backed by a backed by a {@link LinkedList}.<br>
     *     This is useful for {@link AbstractData data objects} with Unknown sizes.
     * </p>
     * <p>
     *     The {@link #get(Object)} methods of the returned {@link SOData} will be in O(n), but the
     *     {@link #add(Object, Object)} and {@link #remove(Object)} methods will be in O(1).
     * </p>
     * @return {@link SOData} backed by a {@link LinkedList}
     * @see #newOrderedDataWithKnownSize(int) for very small (less than 10 elemets) you should generrally use the
     * ArrayList implemenation
     */
    @Contract(pure = true)
    static @NotNull SOData newOrderedDataWithUnknownSize() {
        return new DataListImpl(new LinkedList<>());
    }

    /**
     *
     * @param initialCapacity the initialCapacity of the {@link HashMap} which will back the {@link SOData}
     * @return {@link SOData} backed by a {@link HashMap}
     */
    static @NotNull SOData newHashMapData(int initialCapacity) {
        return new DataMapImpl(new HashMap<>(initialCapacity));
    }
}
