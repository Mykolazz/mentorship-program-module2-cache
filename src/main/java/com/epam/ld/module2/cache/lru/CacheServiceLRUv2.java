package com.epam.ld.module2.cache.lru;

import java.util.*;

public class CacheServiceLRUv2<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public CacheServiceLRUv2(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
