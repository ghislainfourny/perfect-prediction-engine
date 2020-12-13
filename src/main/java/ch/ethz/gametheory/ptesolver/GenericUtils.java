package ch.ethz.gametheory.ptesolver;

import java.lang.reflect.Array;

public class GenericUtils {

    public static <T> T[] getGenericArray(Class<T> clazz, final int size) {
        //noinspection unchecked
        return (T[]) (Array.newInstance(clazz, size));
    }

    public static <T> T[][] getMatrix(Class<T> clazz, final int rows) {
        //noinspection unchecked
        return (T[][]) (Array.newInstance(getGenericArray(clazz, 0).getClass(), rows));
    }

}
