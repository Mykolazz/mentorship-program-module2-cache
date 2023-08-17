package com.epam.ld.module2.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheServiceLRUv2<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_SIZE;

    public CacheServiceLRUv2(int maxSize) {
        super(maxSize, 0.75f, true);
        this.MAX_SIZE = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > MAX_SIZE;
    }
}
