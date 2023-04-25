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

package de.linusdev.data.so;

import de.linusdev.data.entry.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SAOEntryImpl<O> implements Entry<String, O> {

    private final @NotNull String key;
    private @Nullable O value;

    private @Nullable Function<Entry<String, O>, O> getter = null;
    private @Nullable BiConsumer<Entry<String, O>, O> setter = null;

    private SAOEntryImpl(){
        this.key = "";
    }

    public SAOEntryImpl(@NotNull String key, @Nullable O value){
        this.key = key;
        this.value = value;
    }

    public SAOEntryImpl(@NotNull String key){
        this.key = key;
        this.value = null;
    }

    @Override
    public @Nullable O getValue() {
        if(getter != null) return getter.apply(this);
        return value;
    }

    @Override
    public void setValue(@Nullable O value) {
        if(setter != null){
            setter.accept(this, value);
            return;
        }
        this.value = value;
    }

    @Override
    public void overwriteGetValue(@Nullable Function<Entry<String, O>, O> getter) {
        if(this.getter != null) throw new UnsupportedOperationException("This entry can only be contained in a single data.");
        this.getter = getter;
    }

    @Override
    public void overwriteSetValue(@Nullable BiConsumer<Entry<String, O>, O> setter) {
        if(this.setter != null) throw new UnsupportedOperationException("This entry can only be contained in a single data.");
        this.setter = setter;
    }

    public @NotNull String getKey() {
        return key;
    }

    public boolean equalsKey(String key){
        return this.key.equals(key);
    }
}
