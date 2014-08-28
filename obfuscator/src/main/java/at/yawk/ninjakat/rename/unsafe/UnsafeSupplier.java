package at.yawk.ninjakat.rename.unsafe;

import java.util.function.Supplier;

/**
 * @author yawkat
 */
public interface UnsafeSupplier<T> {
    public static <T> UnsafeSupplier<T> of(Supplier<T> predicate) {
        return predicate::get;
    }

    public static <T> Supplier<T> unsafe(UnsafeSupplier<T> predicate) {
        return predicate.unsafe();
    }

    T get() throws Throwable;

    default Supplier<T> unsafe() {
        return () -> {
            try {
                return get();
            } catch (Throwable e) {
                UnsafeAccess.UNSAFE.throwException(e);
                return null; // should never happen
            }
        };
    }
}
