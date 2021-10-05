package me.linusdev.data.parser;

public class ParseTracker {
    int line = 1;

    public ParseTracker(){

    }

    public void nextLine(){
        line++;
    }

    public int getLine() {
        return line;
    }
}
