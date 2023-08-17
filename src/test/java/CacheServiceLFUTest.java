import com.epam.ld.module2.cache.SortStrategy;
import com.epam.ld.module2.cache.nodes.BinaryTreeNode;
import com.epam.ld.module2.cache.CacheServiceLFU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CacheServiceLFUTest extends CacheServiceTest {
    private CacheServiceLFU cacheServiceLFU;

    @BeforeEach
    void setUp() {
        cacheServiceLFU = new CacheServiceLFU(5, 5000);
    }

    @Test
    void testLFUCachePutAndGet() {
        cacheServiceLFU.put("key1", "value1");
        cacheServiceLFU.put("key2", "value2");
        assertEquals("value1", cacheServiceLFU.get("key1"));
        assertEquals("value2", cacheServiceLFU.get("key2"));
        System.out.println("Statistic for 'testLFUCachePutAndGet': {" + "\n"
                + cacheServiceLFU.getStatistic() + " } ");
    }

    @Test
    void testLFUCacheEviction() {
        for (int i = 0; i < 6; i++) {
            cacheServiceLFU.put("key" + i, "value" + i);
           // System.out.println(cacheServiceLFU.cache);
        }
       // System.out.println("Cache evictions before delay: " + cacheServiceLFU.getCacheEvictions());

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // System.out.println("Cache evictions after delay: " + cacheServiceLFU.getCacheEvictions());

        assertNull(cacheServiceLFU.get("key0"));
    }

    @Test
    void testLFUCacheBinarySearch() {
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key1", "key2", "key3", "key4", "key5"));

        for (int i = 1; i <= 5; i++) {
            cacheServiceLFU.put("key" + i, "value" + i);
        }

        assertEquals("value4", cacheServiceLFU.searchUsingRecursiveBinarySearch("key4", sortedKeys, 0, sortedKeys.size() - 1));
        assertEquals("value2", cacheServiceLFU.searchUsingIterativeBinarySearch("key2", sortedKeys));
    }

    @Test
    void testLFUCacheBinarySearchWithSort() {
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key5", "key4", "key3", "key2", "key1"));
        SortStrategy sortStrategy = Collections::sort;

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
            ch++;
        }

        assertEquals("valueB", cacheServiceLFU.searchUsingBinaryTreeBypass("keyB", root));
        assertNull(cacheServiceLFU.searchUsingBinaryTreeBypass("keyF", root));
    }
}
