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

class SpaceOffsetTracker{

    private String offset;
    private final String offsetValue;

    public SpaceOffsetTracker(String offsetValue){
        offset = "";
        this.offsetValue = offsetValue;
    }

    public void add(){
        offset += offsetValue;
    }

    public void remove(){
        offset = offset.substring(0, offset.length()-offsetValue.length());
    }

    public String get(){
        return offset;
    }

    @Override
    public String toString() {
        return offset;
    }
}
