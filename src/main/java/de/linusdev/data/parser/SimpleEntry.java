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

package de.linusdev.data.parser;

import de.linusdev.data.entry.Entry;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SimpleEntry implements Entry<Object, Object> {

    private Object value = null;

    public SimpleEntry(){

    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public void overwriteGetValue(Function<Entry<Object, Object>, Object> getter) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void overwriteSetValue(BiConsumer<Entry<Object, Object>, Object> setter) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Object getKey() {
        return null;
    }

    public Object getValue() {
        return value;
    }
}
