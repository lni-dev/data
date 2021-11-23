package me.linusdev.data;

import me.linusdev.data.converter.Converter;
import me.linusdev.data.converter.ExceptionConverter;
import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * for more information on parsing, look here {@link JsonParser}
 */
public class Data implements Datable, AbstractData, Iterable<Entry> {

    protected ArrayList<Entry> entries;

    public Data(int initialCapacity){
        entries = new ArrayList<>(initialCapacity);
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param key key
     * @param value value
     */
    public void add(String key, Object value){
        entries.add(new Entry(key, value));
    }

    /**
     * Will add this key and value, if value is not {@code null}
     * @param key key
     * @param value value
     * @see #add(String, Object)
     */
    public void addIfNotNull(@NotNull String key, @Nullable Object value){
        if(value != null) add(key, value);
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param entry
     */
    @Deprecated
    public void addEntry(Entry entry){
        entries.add(entry);
    }

    /**
     * adds a new entry if none was found with given key or replaces the old entries value
     *
     * @param key
     * @param value
     * @return the old value or null if there was no entry with given key
     */
    public Object replaceOrAdd(String key, Object value){
        Entry entry = getEntry(key);
        if(entry == null){
            add(key, value);
            return null;
        }

        Object old = entry.getValue();
        entry.setValue(value);
        return old;
    }

    /**
     *
     *
     *
     * @param key
     * @return entry with matching key or null if none was found
     */
    @Nullable
    @Deprecated
    public Entry getEntry(@NotNull String key){
        for(Entry entry : entries){
            if(entry.getKey().equals(key)) return entry;
        }

        return null;
    }

    /**
     * @see #get(String)
     *
     * @param key
     * @param defaultObject
     * @return the first value with given key or defaultObject if no value for this key was found
     */
    public Object get(@NotNull String key, Object defaultObject){

        for(Entry entry : entries){
            if(entry.getKey().equals(key)) return entry.getValue();
        }

        return defaultObject;
    }

    /**
     * @see #get(String)
     *
     * @param key
     * @param defaultObject
     * @return the first value with given key or defaultObject if no value for this key was found or the value was null (if boolean is true)
     */
    public Object get(@NotNull String key, Object defaultObject, boolean returnDefaultIfNull){

        for(Entry entry : entries){
            if(entry.getKey().equals(key)){
                if(returnDefaultIfNull && entry.getValue() == null) return defaultObject;
                else return entry.getValue();
            }
        }

        return defaultObject;
    }

    /**
     * @see #get(String, Object)
     */
    public Object getOrDefault(@NotNull String key, Object defaultObject){
        return get(key, defaultObject);
    }

    /**
     * @see #get(String)
     *
     * @param key
     * @param defaultObject
     * @return the first value with given key or defaultObject if no value for this key was found or defaultObject if the value was null
     */
    public Object getOrDefaultIfNull(@NotNull String key, Object defaultObject){
        return get(key, defaultObject, true);
    }

    /**
     * This returns the value found by the key given. It might be null.<br>
     *
     * The value is an Object! But Arrays can be put into an Data instance as well as Collections,
     * note after parsing a Data the Class might have changed. Arrays are usually represented as ArrayLists when parsed!
     * @see JsonParser
     *
     * @param key
     * @return the first value with given key or null if no value for this key was found
     */
    @Nullable
    public Object get(@NotNull String key){
        return get(key, null);
    }


    /**
     *
     * The value returned by {@link #get(String)} with given key must be of type {@link C}<br>
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param defaultObject object to return if {@link #get(String)} with given key is {@code null}
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(String)} with given is {@code null} or if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(String)} with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    @Nullable
    @Contract("_, _, !null -> !null")
    public <C, R> R getAndConvert(@NotNull String key, @NotNull Converter<C, R> converter, @Nullable R defaultObject){
        C convertible = (C) this.get(key);
        if(convertible == null) return defaultObject;
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(String)} with given key must be of type {@link C}<br>
     * If the value returned by {@link #get(String)} with given key is {@code null}, {@link Converter#convert(Object)} with {@code null} is called!
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param <C> the convertible type
     * @param <R> the result type
     * @return result {@link R} or {@code null} if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(String)} with given key is not of type {@link C}
     */
    @SuppressWarnings("unchecked")
    public <C, R> R getAndConvert(@NotNull String key, @NotNull Converter<C, R> converter){
        C convertible = (C) this.get(key);
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(String)} with given key must be of type {@link C}<br>
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param defaultObject object to return if {@link #get(String)} with given key is {@code null}
     * @param <C> the convertible type
     * @param <R> the result type
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return result {@link R} or {@code null} if defaultObject is {@code null} and {@link #get(String)} with given is {@code null} or if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(String)} with given key is not of type {@link C}
     * @throws E if {@link ExceptionConverter#convert(Object)} throws an Exception
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C, R, E extends Exception> R getAndConvert(@NotNull String key, @NotNull ExceptionConverter<C, R, E> converter, @Nullable R defaultObject) throws E {
        C convertible = (C) this.get(key);
        if(convertible == null) return defaultObject;
        return converter.convert(convertible);
    }

    /**
     *
     * The value returned by {@link #get(String)} with given key must be of type {@link C}<br>
     * If the value returned by {@link #get(String)} with given key is {@code null}, {@link Converter#convert(Object)} with {@code null} is called!
     *
     * @param key the key for the entry of type {@link C}
     * @param converter {@link Converter} to convert from {@link C} to {@link R}
     * @param <C> the convertible type
     * @param <R> the result type
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return result {@link R} or {@code null} if your converter returns {@code null}
     * @throws ClassCastException if the value returned by {@link #get(String)} with given key is not of type {@link C}
     * @throws E if {@link ExceptionConverter#convert(Object)} throws an Exception
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C, R, E extends Exception> R getAndConvert(@NotNull String key, @NotNull ExceptionConverter<C, R, E> converter) throws E {
        C convertible = (C) this.get(key);
        return converter.convert(convertible);
    }

    /**
     * {@link #get(String)} with given key, must be an array.<br>
     * All elements in the array returned by {@link #get(String)} with given key, must be of type {@link C} <br>
     * If any of these conditions is not met, a {@link ClassCastException} might be thrown
     *
     * @param key the key for the array
     * @param converter {@link Converter} to convert the Objects inside the array to {@link R}
     * @param defaultList this will be returned if {@link #get(String)} with given key is {@code null}
     * @param <C> convertible to convert
     * @param <R> result type to convert to
     * @return {@link ArrayList} of {@link R} or {@code null}
     * @throws ClassCastException if types do not match (see method description)
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C, R> ArrayList<R> getAndConvertArrayList(@NotNull String key, Converter<C, R> converter, @Nullable ArrayList<R> defaultList){
        ArrayList<Object> list = (ArrayList<Object>) this.get(key);

        if(list == null) return defaultList;

        ArrayList<R> results = new ArrayList<>(list.size());
        for(Object o : list)
            results.add(converter.convert((C) o));

        return results;
    }

    /**
     * {@link #get(String)} with given key, must be an array.<br>
     * All elements in the array returned by {@link #get(String)} with given key, must be of type {@link C} <br>
     * If any of these conditions is not met, a {@link ClassCastException} might be thrown
     *
     * @param key the key for the array
     * @param converter {@link Converter} to convert the Objects inside the array to {@link R}
     * @param <C> convertible to convert
     * @param <R> result type to convert to
     * @return {@link ArrayList} of {@link R} or {@code null} if no entry with given key exist or is {@code null}
     * @throws ClassCastException if types do not match (see method description)
     * @see #getAndConvertArrayList(String, Converter, ArrayList)
     */
    @Nullable
    public <C, R> ArrayList<R> getAndConvertArrayList(@NotNull String key, Converter<C, R> converter){
        return getAndConvertArrayList(key, converter, null);
    }

    /**
     * {@link #get(String)} with given key, must be an array.<br>
     * All elements in the array returned by {@link #get(String)} with given key, must be of type {@link C} <br>
     * If any of these conditions is not met, a {@link ClassCastException} might be thrown
     *
     * @param key the key for the array
     * @param converter {@link Converter} to convert the Objects inside the array to {@link R}.
     * @param defaultList this will be returned if {@link #get(String)} with given key is {@code null}
     * @param <C> convertible to convert
     * @param <R> result type to convert to
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return {@link ArrayList} of {@link R} or {@code null}
     * @throws ClassCastException if types do not match (see method description)
     * @throws E caused by the converter
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <C, R, E extends Exception> ArrayList<R> getAndConvertArrayList(@NotNull String key, ExceptionConverter<C, R, E> converter, @Nullable ArrayList<R> defaultList) throws E {
        ArrayList<Object> list = (ArrayList<Object>) this.get(key);

        if(list == null) return defaultList;

        ArrayList<R> results = new ArrayList<>(list.size());
        for(Object o : list)
            results.add(converter.convert((C) o));

        return results;
    }

    /**
     * {@link #get(String)} with given key, must be an array.<br>
     * All elements in the array returned by {@link #get(String)} with given key, must be of type {@link C} <br>
     * If any of these conditions is not met, a {@link ClassCastException} might be thrown
     *
     * @param key the key for the array
     * @param converter {@link Converter} to convert the Objects inside the array to {@link R}.
     * @param <C> convertible to convert
     * @param <R> result type to convert to
     * @param <E> the Exception thrown by your {@link ExceptionConverter}
     * @return {@link ArrayList} of {@link R} or {@code null} if entry with given key does not exist or is {@code null}
     * @throws ClassCastException if types do not match (see method description)
     * @throws Exception caused by the converter
     * @see #getAndConvertArrayList(String, ExceptionConverter, ArrayList)
     */
    @Nullable
    public <C, R, E extends Exception> ArrayList<R> getAndConvertArrayList(@NotNull String key, ExceptionConverter<C, R, E> converter) throws E {
        return getAndConvertArrayList(key, converter, null);
    }

    /**
     * removes entry with given key
     * @param key
     * @return old entry or null if there was no entry with given key
     */
    public Entry remove(String key){
        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).getKey().equals(key)) return entries.remove(i);
        }
        return null;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    /**
     * Generates a Json-string of this {@link Data}
     * <br><br>
     * This method creates a new {@link JsonParser} each call. <br>
     * If you are using this a lot in the same Thread, you should probably make your own
     * {@link JsonParser} and call {@link JsonParser#getJsonString(Data)} instead of this method
     * @return json-string of this {@link Data}
     */
    @Override
    public StringBuilder getJsonString(){
        return new JsonParser().getJsonString(this);
    }

    @Override
    public Data getData() {
        return this;
    }

    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }
}
