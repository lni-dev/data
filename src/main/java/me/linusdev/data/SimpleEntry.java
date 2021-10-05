package me.linusdev.data;

public class SimpleEntry {

    private Object value = null;

    public SimpleEntry(){

    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
