package at.yawk.ninjakat.rename.unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import sun.misc.Unsafe;

/**
 * @author yawkat
 */
class UnsafeAccess {
    static final Unsafe UNSAFE;

    static {
        Unsafe found;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            found = (Unsafe) field.get(null);
        } catch (ReflectiveOperationException e) {
            try {
                Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                found = constructor.newInstance();
            } catch (ReflectiveOperationException f) {
                throw new RuntimeException(f);
            }
        }
        UNSAFE = found;
    }
}
