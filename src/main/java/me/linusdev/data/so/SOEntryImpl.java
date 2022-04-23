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

package me.linusdev.data.so;

import me.linusdev.data.entry.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SOEntryImpl implements Entry<String, Object> {

    private final @NotNull String key;
    private @Nullable Object value;

    private @Nullable Function<Entry<String, Object>, Object> getter = null;
    private @Nullable BiConsumer<Entry<String, Object>, Object> setter = null;

    private SOEntryImpl(){
        this.key = "";
    }

    public SOEntryImpl(@NotNull String key, @Nullable Object value){
        this.key = key;
        this.value = value;
    }

    public SOEntryImpl(@NotNull String key){
        this.key = key;
        this.value = null;
    }

    @Override
    public @Nullable Object getValue() {
        if(getter != null) return getter.apply(this);
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        if(setter != null){
            setter.accept(this, value);
            return;
        }
        this.value = value;
    }

    @Override
    public void overwriteGetValue(@Nullable Function<Entry<String, Object>, Object> getter) {
        if(this.getter != null) throw new UnsupportedOperationException("This entry can only be contained in a single data.");
        this.getter = getter;
    }

    @Override
    public void overwriteSetValue(@Nullable BiConsumer<Entry<String, Object>, Object> setter) {
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
