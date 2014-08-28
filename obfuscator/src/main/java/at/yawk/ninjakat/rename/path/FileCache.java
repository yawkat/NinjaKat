package at.yawk.ninjakat.rename.path;

import at.yawk.ninjakat.rename.unsafe.UnsafeFunction;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * A path -> Object cache that auto-expires when the file is changed.
 *
 * @author yawkat
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class FileCache<V> {
    private final LoadingCache<Path, Entry<V>> handle;

    public static <V> FileCache<V> create(UnsafeFunction<Path, V> generator) {
        LoadingCache<Path, Entry<V>> handle = CacheBuilder.newBuilder()
                .softValues()
                .build(new CacheLoader<Path, Entry<V>>() {
                    @Override
                    public Entry<V> load(Path key) throws Exception {
                        while (true) {
                            FileTime beforeLoad = Files.getLastModifiedTime(key);

                            V val = generator.unsafe().apply(key);

                            FileTime afterLoad = Files.getLastModifiedTime(key);

                            // did the file change while the cache was being built?
                            if (beforeLoad.equals(afterLoad)) {
                                return new Entry<>(beforeLoad, val);
                            } else {
                                log.warn("File changed while reading, retrying...");
                            }
                        }
                    }
                });
        return new FileCache<>(handle);
    }

    public V get(Path path) {
        while (true) {
            try {
                // check for file modification
                FileTime mod = Files.getLastModifiedTime(path);
                Entry<V> value = handle.get(path);
                if (value.time.equals(mod)) {
                    return value.value;
                } else {
                    // invalidate cache and retry
                    handle.invalidate(path);
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    @Value
    private static class Entry<V> {
        FileTime time;
        V value;
    }
}
