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

package de.linusdev.data.functions;

/**
 *
 * @param <C> convertible to convert
 * @param <R> result type to convert to
 */
public interface ExceptionConverter<C, R, E extends Throwable> {

    /**
     * converts from {@link C} to {@link R}
     * @param convertible convertible to convert
     * @return converted result
     * @throws E convert exception
     */
    R convert(C convertible) throws E;
}
