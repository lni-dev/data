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

package de.linusdev.data.implemantations;

import de.linusdev.data.entry.Entry;
import de.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SODataListImpl extends SAODataListImpl<Object> implements SOData {
    public SODataListImpl(@NotNull List<Entry<String, Object>> list) {
        super(list);
    }

    @Override
    public @NotNull SOData add(@NotNull String key, Object value) {
        super.add(key, value);
        return this;
    }

    @Override
    public SOData getData() {
        return SOData.super.getData();
    }
}
