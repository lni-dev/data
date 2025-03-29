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

package de.linusdev.data.parser.exceptions;

import de.linusdev.lutils.other.parser.ParseException;
import de.linusdev.lutils.other.parser.ParseTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParseValueException extends ParseException {

    private final Exception exception;
    private final String value;
    private final ParseTracker tracker;

    public ParseValueException(@Nullable Exception e, @Nullable String value, @NotNull ParseTracker tracker){
        this.exception = e;
        this.value = value;
        this.tracker = tracker;
    }

    @Override
    public String getMessage() {
        return "Could not parse value in line  " + tracker.getLine() + "."
                + (value == null ? "" : (" Value: " + value + "."))
                + (exception == null ? "" : (" Exception: " + exception.getClass().getSimpleName() + ": " + exception.getMessage() + "."));
    }
}
