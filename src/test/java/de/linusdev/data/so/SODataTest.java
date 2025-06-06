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

import de.linusdev.data.AbstractData;
import de.linusdev.data.parser.JsonParser;
import de.linusdev.lutils.other.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Test
    void comments() throws IOException, ParseException {
        List<String> comments = new ArrayList<>();
        JsonParser parser = new JsonParser().setAllowComments(true, (jsonParser, s) -> {
            System.out.println("Comment: " + s);
            comments.add(s);
        });

        SOData data = parser.parseString(
                """
                        // First comment
                        {
                        "key1": true,
                        /*comment1
                        
                        multi
                        line!
                         */ "key2" /*comment2*/ : /*comment3*/ "test"
                        }"""
        );

        assertEquals(4, comments.size());
        assertEquals("""
                {
                	"key1": true,
                	"key2": "test"
                }""", data.toJsonString().toString());
    }

    @Test
    void container() throws IOException, ParseException {
        SOData data = AbstractData.PARSER.parseString("""
                {
                \t"test2": "YES",
                \t"test": "test"
                }""");


        var con = data.getContainer("noexist");

        assertFalse(con.exists());
        assertTrue(con.isNull());
        assertNull(con.get());

        assertThrows(NullPointerException.class, con::requireNotNull);

        var con2 = data.getContainer("test2");

        assertTrue(con2.exists());
        assertFalse(con2.isNull());
        assertNotNull(con2.get());

        assertDoesNotThrow(() -> con2.requireNotNull());


    }
}