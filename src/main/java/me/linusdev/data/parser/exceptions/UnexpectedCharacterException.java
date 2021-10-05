package me.linusdev.data.parser.exceptions;


import me.linusdev.data.parser.ParseTracker;

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
