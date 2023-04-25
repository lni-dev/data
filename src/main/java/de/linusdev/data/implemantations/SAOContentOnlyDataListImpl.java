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

package de.linusdev.data.implemantations;

import de.linusdev.data.ContentOnlyData;
import de.linusdev.data.entry.Entry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SAOContentOnlyDataListImpl<O> extends SAODataListImpl<O> implements ContentOnlyData<String, O> {

    public SAOContentOnlyDataListImpl(@NotNull List<Entry<String, O>> list) {
        super(list);
    }
}
