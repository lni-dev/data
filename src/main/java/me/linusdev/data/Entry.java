package me.linusdev.data;

public class Entry extends SimpleEntry{

    private final String key;
    private Object value;

    private Entry(){
        this.key = "";
    }

    public Entry(String key, Object value){
        this.key = key;
        this.value = value;
    }

    public Entry(String key){
        this.key = key;
        this.value = null;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public boolean equalsKey(String key){
        return this.key.equals(key);
    }
}
