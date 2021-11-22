package me.linusdev.data.converter;

/**
 *
 * @param <C> convertible to convert
 * @param <R> result type to convert to
 */
public interface ExceptionConverter<C, R> {

    /**
     * converts from {@link C} to {@link R}
     * @param convertible convertible to convert
     * @return converted result
     * @throws Exception convert exception
     */
    R convert(C convertible) throws Exception;
}
