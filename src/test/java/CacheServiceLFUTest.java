import com.epam.ld.module2.cache.BinaryTreeNode;
import com.epam.ld.module2.cache.lfu.CacheServiceLFU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CacheServiceLFUTest {
    private CacheServiceLFU cacheServiceLFU;

    @BeforeEach
    void setUp() {
        cacheServiceLFU = new CacheServiceLFU();
    }

    @Test
    void testLFUCachePutAndGet() {
        cacheServiceLFU.put("key1", "value1");
        cacheServiceLFU.put("key2", "value2");
        assertEquals("value1", cacheServiceLFU.get("key1"));
        assertEquals("value2", cacheServiceLFU.get("key2"));
    }

    @Test
    void testLFUCacheEviction() {
        // Add a large number of entries to trigger eviction
        for (int i = 0; i < 200000; i++) {
            cacheServiceLFU.put("key" + i, "value" + i);
        }

        // Sleep for some time to allow eviction to take place
        try {
            Thread.sleep(10000); // Sleep for 10 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the evicted key is not present
        assertNull(cacheServiceLFU.get("key0"));
    }

    @Test
    void testLFUCacheBinarySearch() {
        // Simulate cache entries with sorted keys
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key1", "key2", "key3", "key4", "key5"));

        // Add sample data to the cache
        for (int i = 1; i <= 5; i++) {
            cacheServiceLFU.put("key" + i, "value" + i);
        }

        // Call binary search methods and assert results
        assertEquals("value4", cacheServiceLFU.searchUsingRecursiveBinarySearch("key4", sortedKeys, 0, sortedKeys.size() - 1));
        assertEquals("value2", cacheServiceLFU.searchUsingIterativeBinarySearch("key2", sortedKeys));
    }

    @Test
    void testLFUCacheBinarySearchWithSort() {
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key5", "key4", "key3", "key2", "key1"));
        CacheServiceLFU.SortStrategy sortStrategy = Collections::sort; // Natural order sorting
        // Add sample data to the cache
        for (int i = 1; i <= 5; i++) {
            cacheServiceLFU.put("key" + i, "value" + i);
        }
        assertEquals("value3", cacheServiceLFU.searchUsingBinarySearchWithSortStrategy("key3", sortedKeys, sortStrategy));
    }

    @Test
    void testLFUCacheBinaryTreeBypass() {
        BinaryTreeNode root = new BinaryTreeNode("keyB");
        root.left = new BinaryTreeNode("keyA");
        root.right = new BinaryTreeNode("keyC");
        root.left.left = new BinaryTreeNode("keyD");
        root.left.right = new BinaryTreeNode("keyE");

        char ch = 'A';
        for (int i = 1; i <= 5; i++) {
            cacheServiceLFU.put("key" + ch, "value" + ch);
            System.out.println(ch);
            ch++;
        }

        assertEquals("valueB", cacheServiceLFU.searchUsingBinaryTreeBypass("keyB", root));
        assertNull(cacheServiceLFU.searchUsingBinaryTreeBypass("keyF", root));
    }
}