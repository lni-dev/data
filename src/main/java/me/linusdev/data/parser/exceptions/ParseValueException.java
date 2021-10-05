package me.linusdev.data.parser.exceptions;

import me.linusdev.data.parser.ParseTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParseValueException extends ParseException{

    private final Exception exception;
    private final String value;
    private final ParseTracker tracker;

    public ParseValueException(@Nullable Exception e, @Nullable String value, @NotNull ParseTracker tracker){
        this.exception = e;
        this.value = value;
        this.tracker = tracker;
    }

    @Override
    public String getMessage() {
        return "Could not parse value in line  " + tracker.getLine() + "."
                + (value == null ? "" : (" Value: " + value + "."))
                + (exception == null ? "" : (" Exception: " + exception.getClass().getSimpleName() + ": " + exception.getMessage() + "."));
    }
}
