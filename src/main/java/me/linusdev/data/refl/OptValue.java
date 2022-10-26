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

package me.linusdev.data.refl;

import me.linusdev.data.OptionalValue;
import me.linusdev.data.so.SOData;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a custom {@link #value() key} for the annotated field.
 * The annotated field's value must be castable to {@link OptionalValue} or {@code null}.
 * <br><br>
 * {@link AutoSODatable} will work with this annotation like this:
 * <ul>
 *     <li>
 *         If the field's value is {@code null}, the field will be ignored when creating the {@link SOData}.
 *     </li>
 *     <li>
 *         If the field's value is castable to {@link OptionalValue}, the optional's value will be added to the
 *         {@link SOData}, if it {@link OptionalValue#exists() exists}.
 *     </li>
 *     <li>
 *         If the field's value is not castable to {@link OptionalValue}, {@link AutoSODatable#getData()} will throw a
 *         {@link ClassCastException}.
 *     </li>
 * </ul>
 * @see AutoSODatable
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OptValue {

    /**
     * @return The key that should be used for this field.
     */
    @NotNull String value();
}
