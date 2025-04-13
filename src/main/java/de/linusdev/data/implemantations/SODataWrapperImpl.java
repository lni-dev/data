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

import de.linusdev.data.so.SOData;
import de.linusdev.data.so.SODataWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SODataWrapperImpl extends SAODataWrapperImpl<Object> implements SODataWrapper {

    public SODataWrapperImpl(Object obj) {
        super(obj);
    }

    @Override
    public @NotNull SOData add(@NotNull String key, @Nullable Object value) {
        super.add(key, value);
        return this;
    }
}
