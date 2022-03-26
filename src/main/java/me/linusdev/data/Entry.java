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

public class Entry extends SimpleEntry{

    private final String key;
    private Object value;

    private Entry(){
        this.key = "";
    }

    public Entry(String key, Object value){
        this.key = key;
        this.value = value;
    }

    public Entry(String key){
        this.key = key;
        this.value = null;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public boolean equalsKey(String key){
        return this.key.equals(key);
    }
}
