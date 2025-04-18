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

package de.linusdev.data.entry;

import de.linusdev.data.so.SAOEntryImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapEntryImpl<O> extends SAOEntryImpl<O> {

    private final @NotNull Map<String, O> associatedMap;

    public MapEntryImpl(@NotNull Map<String, O> associatedMap, String key) {
        super(key, null);
        this.associatedMap = associatedMap;
    }

    @Override
    public O getValue() {
        return associatedMap.get(getKey());
    }

    @Override
    public void setValue(O value) {
        super.setValue(value);
        associatedMap.put(getKey(), value);
    }

    @Override
    public @NotNull String getKey() {
        return super.getKey();
    }
}
