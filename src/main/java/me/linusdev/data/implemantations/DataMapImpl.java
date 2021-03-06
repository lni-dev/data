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

package me.linusdev.data.implemantations;

import me.linusdev.data.entry.Entry;
import me.linusdev.data.entry.MapEntryImpl;
import me.linusdev.data.so.SOData;
import me.linusdev.data.so.SOEntryImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class DataMapImpl implements SOData {

    protected final Map<String, Object> entries;

    public DataMapImpl(Map<String, Object> map) {
        this.entries = map;
    }

    @Override
    public boolean add(@NotNull String key, @Nullable Object value) {
        entries.put(key, value);
        return true;
    }

    @Override
    public void addEntry(@NotNull Entry<String, Object> entry) {
        entries.put(entry.getKey(), entry.getValue());
        entry.overwriteGetValue(stringObjectEntry -> entries.get(stringObjectEntry.getKey()));
        entry.overwriteSetValue((stringObjectEntry, o) -> {
            entries.put(stringObjectEntry.getKey(), o);
            stringObjectEntry.setValue(o);
        });
    }

    @Override
    public @Nullable Entry<String, Object> remove(@NotNull String key) {
        return new SOEntryImpl(key, entries.remove(key));
    }

    @Override
    public @Nullable Entry<String, Object> getEntry(@NotNull String key) {
        return new MapEntryImpl(this.entries, key);
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @NotNull
    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return new Iterator<>() {
            final Iterator<String> keyIterator = entries.keySet().stream().iterator();

            @Override
            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            @Override
            public Entry<String, Object> next() {
                return new MapEntryImpl(entries, keyIterator.next());
            }
        };
    }
}
