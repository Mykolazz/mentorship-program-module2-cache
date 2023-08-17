package com.epam.ld.module2.cache;

import com.epam.ld.module2.cache.nodes.BinaryTreeNode;
import com.epam.ld.module2.cache.nodes.CacheEntry;
import com.google.common.cache.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheServiceLRU extends CacheService {

    private final Cache<String, CacheEntry> cache = CacheBuilder.newBuilder()
            .maximumSize(maxSize)
            .removalListener((RemovalListener<String, CacheEntry>) notification -> {
                if (notification.getCause() == RemovalCause.SIZE) {
                    System.out.println("Evicted: " + notification.getKey());
                }
            })
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .build();

    public String get(String key) {
        long beginTime = System.nanoTime();
        CacheEntry entry = cache.getIfPresent(key);
        long endTime = System.nanoTime();
        calculateGettingTime(beginTime, endTime);
        return entry != null ? entry.getData() : null;
    }

    public void put(String key, String value) {
        cache.put(key, new CacheEntry(value));
    }

    @Override
    protected void calculatePuttingTime(long begin, long end) {
        long time = end - begin;
        if (time > maxPutTime){
            maxPutTime = time;
        }
        averagePutTime = cache.stats().averageLoadPenalty();
    }

    public int getCacheEvictions() {
        return (int)cache.stats().evictionCount();
    }

    public String searchUsingRecursiveBinarySearch(String key, List<String> sortedKeys, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;
        String midKey = sortedKeys.get(mid);

        if (midKey.equals(key)) {
                CacheEntry entry = cache.getIfPresent(midKey);
                return entry != null ? entry.getData() : null;
        } else if (midKey.compareTo(key) < 0) {
            return searchUsingRecursiveBinarySearch(key, sortedKeys, mid + 1, right);
        } else {
            return searchUsingRecursiveBinarySearch(key, sortedKeys, left, mid - 1);
        }
    }

    public String searchUsingIterativeBinarySearch(String key, List<String> sortedKeys) {
        int left = 0;
        int right = sortedKeys.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            String midKey = sortedKeys.get(mid);
            if (midKey.equals(key)) {
                CacheEntry entry = cache.getIfPresent(midKey);
                return entry != null ? entry.getData() : null;
            } else if (midKey.compareTo(key) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    public String searchUsingBinarySearchWithSortStrategy(String key, List<String> sortedKeys, SortStrategy strategy) {
        strategy.sort(sortedKeys);
        return searchUsingIterativeBinarySearch(key, sortedKeys);
    }

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
            CacheEntry entry = cache.getIfPresent(node.value);
            return entry != null ? entry.getData() : null;
        }
        return searchUsingBinaryTreeBypass(key, node.right, sortedKeys);
    }
}
