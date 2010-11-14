package simulator;

import simulator.parts.Cache;
import simulator.parts.DirectMappedCache;

/**
 *
 * @author Ruben Verhack
 */
public class Simulator {

    public static final int WORD_SIZE = 64;
    public static final int CACHE_SIZE = (int) (2 * Math.pow(2, 20)); // 4MiB
    private CacheType currentCacheType;

    public static enum CacheType {

        DirectMappedCache
    };
    private Cache cache;

    public Simulator() {
        System.out.println("WORD SIZE: " + WORD_SIZE);
        System.out.println("CACHE_SIZE: " + CACHE_SIZE);
        System.out.println("ADDRESSES: " + CACHE_SIZE / WORD_SIZE);
        currentCacheType = CacheType.DirectMappedCache;
        _initCacheType();
    }

    public boolean memoryAccess(int parseInt) {
        return cache.access(parseInt);
    }

    public Cache getCache() {
        return cache;
    }

    public void setCacheType(CacheType cacheType) {
        currentCacheType = cacheType;
    }

    private void _initCacheType() {
        if (currentCacheType == CacheType.DirectMappedCache) {
            cache = new DirectMappedCache();
        }
    }
}
