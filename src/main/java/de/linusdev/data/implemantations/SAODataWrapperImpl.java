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

package de.linusdev.data.implemantations;

import de.linusdev.data.entry.Entry;
import de.linusdev.data.so.SAODataWrapper;
import de.linusdev.data.so.SAOEntryImpl;
import org.jetbrains.annotations.NotNull;

public class SAODataWrapperImpl<O> implements SAODataWrapper<O> {

    private static final String KEY = "o";

    private final SAOEntryImpl<O> entry;

    public SAODataWrapperImpl(O obj) {
        this.entry = new SAOEntryImpl<>(KEY, obj);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public @NotNull Entry<String, O> getEntry(@NotNull String key) {
        return entry;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot clear a wrapper.");
    }
}
