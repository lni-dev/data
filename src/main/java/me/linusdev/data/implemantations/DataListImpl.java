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
import me.linusdev.data.parser.JsonParser;
import me.linusdev.data.so.SOData;
import me.linusdev.data.so.SOEntryImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;


/**
 * for more information on parsing, look here {@link JsonParser}
 */
public class DataListImpl implements SOData {

    protected @NotNull List<Entry<String, Object>> entries;

    public DataListImpl(@NotNull List<Entry<String, Object>> list){
        entries = list;
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param key key
     * @param value value
     */
    @Override
    public boolean add(@NotNull String key, Object value){
        return entries.add(new SOEntryImpl(key, value));
    }

    @Override
    public void addEntry(@NotNull Entry<String, Object> entry){
        entries.add(entry);
    }

    @Override
    public SOEntryImpl getEntry(@NotNull String key){
        for(Entry<String, Object> entry : entries){
            if(entry.getKey().equals(key)) return (SOEntryImpl) entry;
        }

        return null;
    }

    @Override
    public Entry<String, Object> remove(@NotNull String key){
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
    public Iterator<Entry<String, Object>> iterator() {
        return entries.iterator();
    }
}
