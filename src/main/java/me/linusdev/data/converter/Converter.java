package me.linusdev.data.converter;

/**
 *
 * @param <C> convertible to convert
 * @param <R> result type to convert to
 */
public interface Converter<C, R> {

    /**
     * converts from {@link C} to {@link R}
     * @param convertible convertible to convert
     * @return converted result
     */
    R convert(C convertible);
}
