package me.linusdev.data;

/**
 * implement this, if your Object can be simplified to a Object parsable by any {@link Data} parser.
 */
public interface SimpleDatable{

    /**
     *
     * @return must return Byte, Short, Integer, Long, Float, Double, Boolean or Datable
     */
    Object simplify();
}
