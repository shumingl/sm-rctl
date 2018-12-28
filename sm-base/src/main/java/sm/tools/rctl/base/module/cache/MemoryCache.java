package sm.tools.rctl.base.module.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache {

    private static final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    private static Map<String, Object> getOrCreate(String cacheType) {
        return cache.computeIfAbsent(cacheType, value -> new ConcurrentHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String cacheType, String key) {
        return (T) getOrCreate(cacheType).get(key);
    }

    public static void put(String cacheType, String key, Object value) {
        getOrCreate(cacheType).put(key, value);
    }

    public static boolean contains(String cacheType, String key) {
        return getOrCreate(cacheType).containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public static <T> T remove(String cacheType, String key) {
        return (T) getOrCreate(cacheType).remove(key);
    }
}
