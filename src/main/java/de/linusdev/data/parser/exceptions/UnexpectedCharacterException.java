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

package de.linusdev.data.parser.exceptions;


import de.linusdev.data.parser.ParseTracker;

public class UnexpectedCharacterException extends ParseException {
    private char invalidChar;
    private int line;

    public UnexpectedCharacterException(char invalidChar, ParseTracker tracker){
        this.invalidChar = invalidChar;
        this.line = tracker.getLine();
    }

    @Override
    public String getMessage() {
        return "Invalid character in line " + line + ": " + invalidChar + "(0x" + Integer.toHexString(invalidChar) + ")";
    }
}
