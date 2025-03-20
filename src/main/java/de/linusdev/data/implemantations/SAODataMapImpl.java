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

package de.linusdev.data.implemantations;

import de.linusdev.data.entry.Entry;
import de.linusdev.data.entry.MapEntryImpl;
import de.linusdev.data.so.SAOData;
import de.linusdev.data.so.SAOEntryImpl;
import de.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class SAODataMapImpl<O> implements SAOData<O> {

    protected final Map<String, O> entries;

    public SAODataMapImpl(Map<String, O> map) {
        this.entries = map;
    }

    @Override
    public boolean add(@NotNull String key, @Nullable O value) {
        entries.put(key, value);
        return true;
    }

    @Override
    public void addEntry(@NotNull Entry<String, O> entry) {
        entries.put(entry.getKey(), entry.getValue());
        entry.overwriteGetValue(stringObjectEntry -> entries.get(stringObjectEntry.getKey()));
        entry.overwriteSetValue((stringObjectEntry, o) -> {
            entries.put(stringObjectEntry.getKey(), o);
            stringObjectEntry.setValue(o);
        });
    }

    @Override
    public @Nullable Entry<String, O> remove(@NotNull String key) {
        return new SAOEntryImpl<>(key, entries.remove(key));
    }

    @Override
    public @Nullable Entry<String, O> getEntry(@NotNull String key) {
        return new MapEntryImpl<O>(this.entries, key);
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public void clear() {
        entries.clear();
    }

    @NotNull
    @Override
    public Iterator<Entry<String, O>> iterator() {
        return new Iterator<>() {
            final Iterator<String> keyIterator = entries.keySet().stream().iterator();

            @Override
            public boolean hasNext() {
                return keyIterator.hasNext();
            }

            @Override
            public Entry<String, O> next() {
                return new MapEntryImpl<>(entries, keyIterator.next());
            }
        };
    }
}
