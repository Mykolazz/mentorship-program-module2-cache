package com.epam.ld.module2.cache.lfu;

import com.epam.ld.module2.cache.BinaryTreeNode;
import com.epam.ld.module2.cache.CacheEntry;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class CacheServiceLFU {
    private static final int MAX_SIZE = 1000;
    private static final long EVICTION_TIME_MS = 5000;
    private static final Logger logger = Logger.getLogger(CacheServiceLFU.class.getName());

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> accessCount = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int cacheEvictions = 0;

    public CacheServiceLFU() {
        scheduler.scheduleAtFixedRate(this::evict, EVICTION_TIME_MS, EVICTION_TIME_MS, TimeUnit.MILLISECONDS);
    }

    public String get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            accessCount.put(key, accessCount.getOrDefault(key, 0L) + 1);
            return entry.getData();
        }
        return null;
    }

    public void put(String key, String value) {
        if (cache.size() >= MAX_SIZE) {
            evict();
        }
        cache.put(key, new CacheEntry(value));
        accessCount.put(key, 1L);
    }

    private void evict() {
        if (cache.size() >= MAX_SIZE) {
            String leastUsedKey = accessCount.entrySet().stream()
                    .min(Comparator.comparingLong(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .orElse(null);

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

    /**
     * Search cache entry using recursively binary search algorithm.
     * @param key The key to search for.
     * @param sortedKeys The sorted keys to perform binary search on.
     * @param left The left index for the search interval.
     * @param right The right index for the search interval.
     * @return The value associated with the key, or null if not found.
     */
    public String searchUsingRecursiveBinarySearch(String key, List<String> sortedKeys, int left, int right) {
        if (left <= right) {
            int mid = left + (right - left) / 2;  // Calculate mid index
            String midKey = sortedKeys.get(mid);   // Get the key at mid index

            if (midKey.equals(key)) {  // Key found at mid index
                return cache.get(midKey).getData();  // Return data associated with the key
            } else if (midKey.compareTo(key) > 0) {  // Key is smaller, search in left half
                return searchUsingRecursiveBinarySearch(key, sortedKeys, left, mid - 1);
            } else {  // Key is larger, search in right half
                return searchUsingRecursiveBinarySearch(key, sortedKeys, mid + 1, right);
            }
        }
        return null;  // Key not found, return null
    }

    /**
     * Search cache entry using iteratively binary search algorithm.
     * @param key The key to search for.
     * @param sortedKeys The sorted keys to perform binary search on.
     * @return The value associated with the key, or null if not found.
     */
    public String searchUsingIterativeBinarySearch(String key, List<String> sortedKeys) {
        int left = 0;
        int right = sortedKeys.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midKey = sortedKeys.get(mid);

            if (midKey.equals(key)) {
                return cache.get(midKey).getData();
            } else if (midKey.compareTo(key) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    /**
     * Integrates sorting and binary search using a specified sort strategy.
     * @param key The key to search for.
     * @param sortedKeys The sorted keys to perform binary search on.
     * @param strategy The sort strategy to use for sorting the keys.
     * @return The value associated with the key, or null if not found.
     */
    public String searchUsingBinarySearchWithSortStrategy(String key, List<String> sortedKeys, SortStrategy strategy) {
        strategy.sort(sortedKeys);
        return searchUsingIterativeBinarySearch(key, sortedKeys);
    }

    /**
     * Search cache entry using binary tree bypass (in-order traversal) algorithm.
     * @param key The key to search for.
     * @param rootNode The root of the binary search tree (cache keys are used as BST nodes).
     * @return The value associated with the key, or null if not found.
     */
    public String searchUsingBinaryTreeBypass(String key, BinaryTreeNode rootNode) {
        return searchUsingBinaryTreeBypass(key, rootNode, new ArrayList<>());
    }

    private String searchUsingBinaryTreeBypass(String key, BinaryTreeNode node, List<String> sortedKeys) {
        if (node == null) {
            return null;
        }

        String leftResult = searchUsingBinaryTreeBypass(key, node.left, sortedKeys);
        if (leftResult != null) {
            return leftResult;
        }

        sortedKeys.add(node.value);

        if (node.value.equals(key)) {
            return cache.get(node.value).getData();
        }

        return searchUsingBinaryTreeBypass(key, node.right, sortedKeys);
    }

    // ... other methods ...

    public interface SortStrategy {
        void sort(List<String> list);
    }
}
