package at.yawk.ninjakat.rename.unsafe;

import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface UnsafePredicate<T> {
    public static <T> UnsafePredicate<T> of(Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> unsafe(UnsafePredicate<T> predicate) {
        return predicate.unsafe();
    }

    boolean test(T t) throws Throwable;

    default Predicate<T> unsafe() {
        return t -> {
            try {
                return test(t);
            } catch (Throwable e) {
                UnsafeAccess.UNSAFE.throwException(e);
                return false; // should never happen
            }
        };
    }
}
