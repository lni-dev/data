package me.linusdev.data.converter;

/**
 *
 * @param <C> convertible to convert
 * @param <R> result type to convert to
 */
public interface ExceptionConverter<C, R, E extends Exception> {

    /**
     * converts from {@link C} to {@link R}
     * @param convertible convertible to convert
     * @return converted result
     * @throws E convert exception
     */
    R convert(C convertible) throws E;
}
