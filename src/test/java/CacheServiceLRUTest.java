import com.epam.ld.module2.cache.SortStrategy;
import com.epam.ld.module2.cache.nodes.BinaryTreeNode;
import com.epam.ld.module2.cache.CacheServiceLRU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceLRUTest extends CacheServiceTest {
    private CacheServiceLRU cacheServiceLRU;

    @BeforeEach
    void setUp() {
        cacheServiceLRU = new CacheServiceLRU();
    }

    @Test
    void testLRUCachePutAndGet() {
        cacheServiceLRU.put("key1", "value1");
        cacheServiceLRU.put("key2", "value2");
        assertEquals("value1", cacheServiceLRU.get("key1"));
        assertEquals("value2", cacheServiceLRU.get("key2"));
        System.out.println("Statistic for 'testLRUCachePutAndGet'" + "\n"
                + cacheServiceLRU.getStatistic());
    }

    @Test
    void testLRUCacheEviction() {
        for (int i = 0; i < 100010; i++) {
            cacheServiceLRU.put("key" + i, "value" + i);
        }

        try {
            Thread.sleep(10); // Sleep for n seconds (adjust if needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(cacheServiceLRU.get("key0"));
    }

    @Test
    void testLRUCacheBinarySearch() {
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key1", "key2", "key3", "key4", "key5"));

        for (int i = 1; i <= 5; i++) {
            cacheServiceLRU.put("key" + i, "value" + i);
        }

        assertEquals("value4", cacheServiceLRU.searchUsingRecursiveBinarySearch("key4", sortedKeys, 0, sortedKeys.size() - 1));
        assertEquals("value2", cacheServiceLRU.searchUsingIterativeBinarySearch("key2", sortedKeys));
    }

    @Test
    void testLRUCacheBinarySearchWithSort() {
        List<String> sortedKeys = new ArrayList<>(Arrays.asList("key5", "key4", "key3", "key2", "key1"));
        SortStrategy sortStrategy = Collections::sort;

        for (int i = 1; i <= 5; i++) {
            cacheServiceLRU.put("key" + i, "value" + i);
        }
        assertEquals("value3", cacheServiceLRU.searchUsingBinarySearchWithSortStrategy("key3", sortedKeys, sortStrategy));
    }

    @Test
    void testLRUCacheBinaryTreeBypass() {
        BinaryTreeNode root = new BinaryTreeNode("keyB");
        root.left = new BinaryTreeNode("keyA");
        root.right = new BinaryTreeNode("keyC");
        root.left.left = new BinaryTreeNode("keyD");
        root.left.right = new BinaryTreeNode("keyE");

        char ch = 'A';
        for (int i = 1; i <= 5; i++) {
            cacheServiceLRU.put("key" + ch, "value" + ch);
            System.out.println("char " + ch);
            ch++;
        }
        assertEquals("valueB", cacheServiceLRU.searchUsingBinaryTreeBypass("keyB", root));
        assertNull(cacheServiceLRU.searchUsingBinaryTreeBypass("keyF", root));
    }
}

