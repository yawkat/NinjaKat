package at.yawk.ninjakat.rename.unsafe;

import java.util.function.Function;

/**
 * @author yawkat
 */
public interface UnsafeFunction<T, R> {
    public static <T, R> UnsafeFunction<T, R> of(Function<T, R> function) {
        return function::apply;
    }

    public static <T, R> Function<T, R> unsafe(UnsafeFunction<T, R> function) {
        return function.unsafe();
    }

    R apply(T t) throws Exception;

    default Function<T, R> unsafe() {
        return t -> {
            try {
                return apply(t);
            } catch (Exception e) {
                UnsafeAccess.UNSAFE.throwException(e);
                return null; // should never happen
            }
        };
    }
}
