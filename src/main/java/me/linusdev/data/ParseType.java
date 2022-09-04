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

import org.jetbrains.annotations.ApiStatus;

/**
 * How a {@link AbstractData} will be parsed depends on its {@link ParseType}
 */
@ApiStatus.Internal
public enum ParseType {
    /**
     * {@link AbstractData} will be parsed to a normal json-object
     */
    NORMAL,

    /**
     * only the entries of the {@link AbstractData} will be parsed. That means the entry will not be wrapped by
     * a json-object. If more than one entry is present, they will be separated by a comma.
     */
    CONTENT_ONLY,
}
