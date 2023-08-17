package com.epam.ld.module2.cache;

import com.epam.ld.module2.cache.nodes.BinaryTreeNode;
import com.epam.ld.module2.cache.nodes.CacheEntry;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class CacheServiceLFU extends CacheService {
    private final int DEFAULT_EVICTION_TIME_MS = 5000;
    private final int evictionTimeMillis;
    private static final Logger logger = Logger.getLogger(CacheServiceLFU.class.getName());

    public final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    protected final Map<String, Long> accessCount = new ConcurrentHashMap<>();
    private final Map<String, Long> accessOrder = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CacheServiceLFU() {
        this.evictionTimeMillis = DEFAULT_EVICTION_TIME_MS;
       // scheduler.scheduleAtFixedRate(this::evict, DEFAULT_EVICTION_TIME_MS, DEFAULT_EVICTION_TIME_MS, TimeUnit.MILLISECONDS);
    }

    public CacheServiceLFU(int size, int expirationInMillis) {
        super(size > 0 ? size : DEFAULT_MAX_SIZE);
        this.evictionTimeMillis =
                expirationInMillis > 0 ? expirationInMillis : DEFAULT_EVICTION_TIME_MS;
       // scheduler.scheduleAtFixedRate(this::evict, evictionTimeMillis, evictionTimeMillis, TimeUnit.MILLISECONDS);
    }

    public String get(String key) {
        long beginTime = System.nanoTime();
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            long currentAccessCount = accessCount.getOrDefault(key, 0L);
            accessCount.put(key, currentAccessCount + 1);
            accessOrder.put(key, System.nanoTime());
            long endTime = System.nanoTime();
            calculateGettingTime(beginTime, endTime);
            getsCounter++;
            return entry.getData();
        }
        return null;
    }

    public void put(String key, String value) {
        long beginTime = System.nanoTime();
        if (cache.size() >= maxSize) {
            evict();
        }
        cache.put(key, new CacheEntry(value));
        accessCount.put(key, 1L);
        accessOrder.put(key, System.nanoTime());
        long endTime = System.nanoTime();
        putsCounter++;
        calculatePuttingTime(beginTime, endTime);
    }

    protected void evict() {
        if (cache.size() >= maxSize) {
            String leastUsedKey = accessCount.entrySet().stream()
                    .min(Comparator.comparingLong((Map.Entry<String, Long> e) -> accessCount.get(e.getKey()))
                            .thenComparing(e -> accessOrder.getOrDefault(e.getKey(), Long.MIN_VALUE)))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (leastUsedKey != null) {
                logger.info("Evicting key: " + leastUsedKey);
                cache.remove(leastUsedKey);
                accessCount.remove(leastUsedKey);
                accessOrder.remove(leastUsedKey);
                cacheEvictions++;
            }
        }
    }

    public String searchUsingRecursiveBinarySearch(String key, List<String> sortedKeys, int left, int right) {
        if (left <= right) {
            int mid = left + (right - left) / 2;
            String midKey = sortedKeys.get(mid);

            if (midKey.equals(key)) {
                return cache.get(midKey).getData();
            } else if (midKey.compareTo(key) > 0) {
                return searchUsingRecursiveBinarySearch(key, sortedKeys, left, mid - 1);
            } else {
                return searchUsingRecursiveBinarySearch(key, sortedKeys, mid + 1, right);
            }
        }
        return null;
    }

    public String searchUsingIterativeBinarySearch(String key, List<String> sortedKeys) {
        int left = 0;
        int right = sortedKeys.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midKey = sortedKeys.get(mid);

            if (midKey.equals(key)) {
                return cache.get(midKey).getData();
            } else if (midKey.compareTo(key) > 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return null;
    }

    public String searchUsingBinarySearchWithSortStrategy(String key, List<String> sortedKeys, SortStrategy strategy) {
        strategy.sort(sortedKeys);
        return searchUsingIterativeBinarySearch(key, sortedKeys);
    }

    public String searchUsingBinaryTreeBypass(String key, BinaryTreeNode root) {
        if (root == null) {
            return null;
        }

        if (key.equals(root.value)) {
            return cache.get(root.value).getData();
        }

        String leftResult = searchUsingBinaryTreeBypass(key, root.left);
        if (leftResult != null) {
            return leftResult;
        }

        return searchUsingBinaryTreeBypass(key, root.right);
    }
}
