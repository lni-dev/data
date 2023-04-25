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

package de.linusdev.data.refl;

import de.linusdev.data.OptionalValue;
import de.linusdev.data.so.SOData;
import de.linusdev.data.so.SODatable;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * If your class implements this interface, its {@link #getData()} method
 * will be automatically add all non-transient fields using reflection.
 * For more information see {@link #getData(SOData)}
 */
public interface AutoSODatable extends SODatable {

    /**
     * Adds all {@link Class#getDeclaredFields() fields} of the implementing class, which do not have
     * the {@link Modifier#isTransient(int) transient} modifier.
     * <br><br>
     * This interface supports the {@link Value} and {@link OptValue} annotations. But it will
     * add all non-transient fields to the data, even if they are not annotated.
     *
     * @param data existing {@link SOData} to add the fields to or {@code null}.
     * @return {@link SOData} with the fields added as specified above.
     */
    default SOData getData(@Nullable SOData data) {
        Field[] fields = this.getClass().getDeclaredFields();
        if(data == null) data = SOData.newOrderedDataWithKnownSize(fields.length);

        for(Field field : fields) {
            int mods = field.getModifiers();
            if(Modifier.isTransient(mods) || Modifier.isStatic(mods)) continue;
            if(!Modifier.isPublic(mods)) field.setAccessible(true);

            Value valueAnno = field.getAnnotation(Value.class);
            OptValue optAnno = field.getAnnotation(OptValue.class);
            String key =
                    optAnno == null
                            ? (valueAnno == null ? field.getName() : valueAnno.value())
                            : optAnno.value();

            try {
                Object value = field.get(this);


                if (optAnno != null) {
                    if(value != null) data.addIfOptionalExists(key, (OptionalValue<?>) value);

                } else if(valueAnno != null && !valueAnno.addIfNull()) {
                    data.addIfNotNull(key, value);
                } else {
                    data.add(key, value);
                }


            } catch (IllegalAccessException ignored) {
                System.err.println("Cannot access field '" + field.getName() + "' in class '"  + this.getClass().getCanonicalName() + "'.");
            }
        }

        return data;
    }

    /**
     * Functions the same as {@link #getData(SOData) getData(null)}.
     * @return {@link SOData} as specified by {@link #getData(SOData)}.
     */
    @Override
    default SOData getData() {
        return getData(null);
    }
}
