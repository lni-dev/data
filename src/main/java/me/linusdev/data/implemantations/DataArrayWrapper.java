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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataArrayWrapper<A> extends SAOContentOnlyDataListImpl<A[]>{

    public static final String ARRAY_KEY = "array";

    public DataArrayWrapper(A[] array) {
        super(List.of(new ArrayWrapperEntry<>(ARRAY_KEY, array)));
    }

    public void set(@NotNull A[] array){
        //noinspection ConstantConditions
        getEntry(ARRAY_KEY).setValue(array);
    }

    public A[] get(){
        return get(ARRAY_KEY);
    }

    @Override
    public boolean add(@NotNull String key, A[] value) {
        throw new UnsupportedOperationException("This data can only have a single entry, use the set method instead");
    }

    @Override
    public void addEntry(@NotNull Entry<String, A[]> entry) {
        throw new UnsupportedOperationException("This data can only have a single entry, use the set method instead");
    }

    @Override
    public Entry<String, A[]> getEntry(@NotNull String key) {
        return super.getEntry(ARRAY_KEY);
    }

    @Override
    public Entry<String, A[]> remove(@NotNull String key) {
        throw new UnsupportedOperationException("This data can only have a single entry, which cannot be removed. use the set method instead");
    }

    public static class ArrayWrapperEntry<A> implements Entry<String, A[]> {

        private final @NotNull String key;
        private A[] array;

        public ArrayWrapperEntry(@NotNull String key, A[] array) {
            this.key = key;
            this.array = array;
        }


        @Override
        public A[] getValue() {
            return array;
        }

        @Override
        public void setValue(A[] value) {
            this.array = value;
        }

        @Override
        public void overwriteGetValue(@Nullable Function<Entry<String, A[]>, A[]> getter) {
            throw new UnsupportedOperationException("cannot Overwrite");
        }

        @Override
        public void overwriteSetValue(@Nullable BiConsumer<Entry<String, A[]>, A[]> setter) {
            throw new UnsupportedOperationException("cannot Overwrite");
        }

        @Override
        public String getKey() {
            return key;
        }
    }


}
