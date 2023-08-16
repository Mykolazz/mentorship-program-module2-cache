package com.epam.ld.module2.cache.lfu;

import com.epam.ld.module2.cache.CacheEntry;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class CacheServiceLFUv2 {
        private static final int MAX_SIZE = 100000;
        private static final Logger logger = Logger.getLogger(CacheServiceLFUv2.class.getName());

        private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
        private final Map<String, Integer> accessCount = new ConcurrentHashMap<>();
        private final PriorityQueue<String> evictionQueue = new PriorityQueue<>(Comparator.comparingInt(accessCount::get));
        private int cacheEvictions = 0;

        public String get(String key) {
            CacheEntry entry = cache.get(key);
            if (entry != null) {
                accessCount.put(key, accessCount.getOrDefault(key, 0) + 1);
                evictionQueue.remove(key); // Remove from queue and re-add to maintain priority
                evictionQueue.add(key);
                return entry.getData();
            }
            return null;
        }

        public void put(String key, String value) {
            if (cache.size() >= MAX_SIZE) {
                evict();
            }
            cache.put(key, new CacheEntry(value));
            accessCount.put(key, 1);
            evictionQueue.add(key);
        }

        private void evict() {
            while (cache.size() >= MAX_SIZE) {
                String leastUsedKey = evictionQueue.poll();
                if (leastUsedKey != null) {
                    logger.info("Evicting key: " + leastUsedKey);
                    cache.remove(leastUsedKey);
                    accessCount.remove(leastUsedKey);
                    cacheEvictions++;
                }
            }
        }

        public int getCacheEvictions() {
            return cacheEvictions;
        }

}
