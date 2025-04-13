/*
 * Copyright (c) 2025 Linus Andera
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

package de.linusdev.data.so;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class SODataTest {

    @Test
    void mapValue() {
        SOData data = SOData.newOrderedDataWithKnownSize(3);

        data.add("test", 1234);

        Map<String, Object> testMap = Map.of("key1", "wow", "key2", 50L);

        data.add("testMap", testMap);

        assertEquals("""
                {
                	"test": 1234,
                	"testMap": {
                		"key2": 50,
                		"key1": "wow"
                	}
                }""", data.toJsonString().toString());

    }

    @Test
    void computeIfAbsent() {
        SOData data = SOData.newHashMapData(16);

        data.add("test", "test");
        AtomicBoolean run = new AtomicBoolean(false);
        data.computeIfAbsent("test", s -> {
            run.set(true);
            return "NO";
        });
        assertFalse(run.get());

        data.computeIfAbsent("test2", s -> {
            run.set(true);
            return "YES";
        });
        assertTrue(run.get());

        assertEquals("""
                {
                \t"test2": "YES",
                \t"test": "test"
                }""", data.toJsonString().toString());
    }
}