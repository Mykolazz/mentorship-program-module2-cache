import com.epam.ld.module2.cache.CacheServiceLRUv2;
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
        for (int i = 0; i < numberOfAdditions; i++) {
            cacheServiceLRUv2.put("key" + i, "value" + i);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(cacheServiceLRUv2.get("key0"));
    }
}
