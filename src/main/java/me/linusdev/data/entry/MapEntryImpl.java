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

package me.linusdev.data.entry;

import me.linusdev.data.so.SAOEntryImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapEntryImpl extends SAOEntryImpl<Object> {

    private final @NotNull Map<String, Object> associatedMap;

    public MapEntryImpl(@NotNull Map<String, Object> associatedMap, String key) {
        super(key, null);
        this.associatedMap = associatedMap;
    }

    @Override
    public Object getValue() {
        return associatedMap.get(getKey());
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        associatedMap.put(getKey(), value);
    }

    @Override
    public @NotNull String getKey() {
        return super.getKey();
    }
}
