package me.linusdev.data;

import me.linusdev.data.parser.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;


/**
 * for more information on parsing, look here {@link JsonParser}
 */
public class Data implements Datable, AbstractData, Iterable<Entry> {

    public static JsonParser jsonParser = new JsonParser();

    protected ArrayList<Entry> entries;

    public Data(int initialCapacity){
        entries = new ArrayList<>(initialCapacity);
    }

    /**
     * Adds a new entry, does NOT check if an entry with given key already exists!
     * @param key
     * @param value
     */
    public void add(String key, Object value){
        entries.add(new Entry(key, value));
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
     * This returns the value found by the key given. It might be null.
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

    @Override
    public StringBuilder getJsonString(){
        return jsonParser.getJsonString(this);
    }

    public static void setJsonParser(JsonParser jsonParser) {
        Data.jsonParser = jsonParser;
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
