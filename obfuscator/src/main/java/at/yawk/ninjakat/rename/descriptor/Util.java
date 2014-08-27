package at.yawk.ninjakat.rename.descriptor;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author yawkat
 */
class Util {
    private static final Collector<Object, StringBuilder, String> TO_STRING =
            new Collector<Object, StringBuilder, String>() {
                @Override
                public Supplier<StringBuilder> supplier() {
                    return StringBuilder::new;
                }

                @Override
                public BiConsumer<StringBuilder, Object> accumulator() {
                    return StringBuilder::append;
                }

                @Override
                public BinaryOperator<StringBuilder> combiner() {
                    return StringBuilder::append;
                }

                @Override
                public Function<StringBuilder, String> finisher() {
                    return StringBuilder::toString;
                }

                @Override
                public Set<Characteristics> characteristics() {
                    return Collections.emptySet();
                }
            };

    @SuppressWarnings("unchecked")
    public static <T> Collector<T, ?, String> toStringCollector() {
        return (Collector<T, ?, String>) TO_STRING;
    }
}
