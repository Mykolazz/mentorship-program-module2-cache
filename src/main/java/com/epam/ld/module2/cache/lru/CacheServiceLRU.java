package com.epam.ld.module2.cache.lru;

import com.epam.ld.module2.cache.BinaryTreeNode;
import com.epam.ld.module2.cache.CacheEntry;
import com.google.common.cache.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheServiceLRU {
    private int MAX_SIZE;

    public CacheServiceLRU(int maxSize){
        this.MAX_SIZE = maxSize;
    }

    private final Cache<String, CacheEntry> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_SIZE)
            .removalListener((RemovalListener<String, CacheEntry>) notification -> {
                if (notification.getCause() == RemovalCause.SIZE) {
                    System.out.println("Evicted: " + notification.getKey());
                }
            })
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    public String get(String key) {
        CacheEntry entry = cache.getIfPresent(key);
        return entry != null ? entry.getData() : null;
    }

    public void put(String key, String value) {
        cache.put(key, new CacheEntry(value));
    }

    public double averagePutTimeNs() {
        return cache.stats().averageLoadPenalty();
    }

    public long getCacheEvictions() {
        return cache.stats().evictionCount();
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
        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;
        String midKey = sortedKeys.get(mid);

        if (midKey.equals(key)) {
            return cache.getIfPresent(midKey).getData();
        } else if (midKey.compareTo(key) < 0) {
            return searchUsingRecursiveBinarySearch(key, sortedKeys, mid + 1, right);
        } else {
            return searchUsingRecursiveBinarySearch(key, sortedKeys, left, mid - 1);
        }
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
                return cache.getIfPresent(midKey).getData();
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
            return cache.getIfPresent(node.value).getData();
        }

        return searchUsingBinaryTreeBypass(key, node.right, sortedKeys);
    }

    // ... other methods ...

    public interface SortStrategy {
        void sort(List<String> list);
    }
}
