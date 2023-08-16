import com.epam.ld.module2.cache.lru.CacheServiceLRUv2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CacheServiceLRUv2Test extends CacheServiceTest {
    private CacheServiceLRUv2<String, String> cacheServiceLRUv2;

    @BeforeEach
    void setUp() {
        cacheServiceLRUv2 = new CacheServiceLRUv2<>(cacheMaxSize);
    }

    @Test
    void testLRUCachePutAndGet() {
        cacheServiceLRUv2.put("key1", "value1");
        cacheServiceLRUv2.put("key2", "value2");
        assertEquals("value1", cacheServiceLRUv2.get("key1"));
        assertEquals("value2", cacheServiceLRUv2.get("key2"));
    }

    @Test
    void testLRUCacheEviction() {
        // Add a large number of entries to trigger eviction
        for (int i = 0; i < numberOfAdditions; i++) {
            cacheServiceLRUv2.put("key" + i, "value" + i);
        }

        // Sleep for some time to allow eviction to take place
        try {
            Thread.sleep(delay); // Sleep for 10 seconds (adjust as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the evicted key is not present
        assertNull(cacheServiceLRUv2.get("key0"));
    }
}
