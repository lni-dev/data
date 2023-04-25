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
import de.linusdev.data.so.SAOData;
import de.linusdev.data.so.SAOEntryImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;


public class SAODataListImpl<O> implements SAOData<O> {

    protected @NotNull List<Entry<String, O>> entries;

    public SAODataListImpl(@NotNull List<Entry<String, O>> list){
        entries = list;
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param key key
     * @param value value
     */
    @Override
    public boolean add(@NotNull String key, O value){
        return entries.add(new SAOEntryImpl<>(key, value));
    }

    @Override
    public void addEntry(@NotNull Entry<String, O> entry){
        entries.add(entry);
    }

    @Override
    public Entry<String, O> getEntry(@NotNull String key){
        for(Entry<String, O> entry : entries){
            if(entry.getKey().equals(key)) return entry;
        }

        return null;
    }

    @Override
    public Entry<String, O> remove(@NotNull String key){
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getKey().equals(key)) return entries.remove(i);
        }
        return null;
    }


    @Override
    public boolean isEmpty(){
        return entries.isEmpty();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Iterator<Entry<String, O>> iterator() {
        return entries.iterator();
    }
}
