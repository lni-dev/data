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

package me.linusdev.data.functions;

/**
 *
 * @param <E> Exception that can be thrown
 * @param <V> value class
 * @param <K> key class
 */
public interface ValueFactory<K, V, E extends Exception> {

    /**
     *
     * @param key the key of the entry, the factory is for
     * @return {@link V}
     * @throws E factory {@link E}
     */
    V create(K key) throws E;

}
