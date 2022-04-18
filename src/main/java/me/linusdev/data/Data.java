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

package me.linusdev.data;

import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.entry.Entry;
import me.linusdev.data.entry.EntryImpl;
import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;


/**
 * for more information on parsing, look here {@link JsonParser}
 */
public class Data implements Datable, AbstractData<String, Object>, Iterable<EntryImpl> {

    protected ArrayList<Entry<String, Object>> entries;

    public Data(int initialCapacity){
        entries = new ArrayList<>(initialCapacity);
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param key key
     * @param value value
     */
    public boolean add(@NotNull String key, Object value){
        return entries.add(new EntryImpl(key, value));
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param entry
     */
    @Deprecated
    public void addEntry(EntryImpl entry){
        entries.add(entry);
    }

    /**
     * @param key
     * @return entry with matching key or null if none was found
     */
    public EntryImpl getEntry(@NotNull String key){
        for(Entry<String, Object> entry : entries){
            if(entry.getKey().equals(key)) return (EntryImpl) entry;
        }

        return null;
    }

    /**
     * removes entry with given key
     * @param key
     * @return old entry or null if there was no entry with given key
     */
    public EntryImpl remove(String key){
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getKey().equals(key)) return (EntryImpl) entries.remove(i);
        }
        return null;
    }

    /**
     *
     * @return {@code true} if this data does not contain any entries, false otherwise
     */
    public boolean isEmpty(){
        return entries.isEmpty();
    }

    public ArrayList<EntryImpl> getEntries() {
        return entries;
    }

    /**
     * Generates a Json-string of this {@link Data}
     * <br><br>
     * This method creates a new {@link JsonParser} each call. <br>
     * If you are using this a lot in the same Thread, you should probably make your own
     * {@link JsonParser} and call {@link JsonParser#getJsonString(Data)} instead of this method
     * @return json-string of this {@link Data}
     */
    @Override
    public StringBuilder getJsonString(){
        return new JsonParser().getJsonString(this);
    }

    @Override
    public Data getData() {
        return this;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return entries.iterator();
    }
}
