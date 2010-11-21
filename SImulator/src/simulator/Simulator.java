package simulator;

import java.io.File;
import simulator.parts.Cache;
import simulator.parts.DirectMappedCache;
import simulator.prefetchers.LinearPrefetch;
import simulator.prefetchers.ScalablePrefetch;
import simulator.victimcaches.PlainVictimCache;

/**
 *
 * @author Ruben Verhack
 */
public class Simulator {

    /**
     *
     */
    public static final int WORD_SIZE = 64;
    /**
     *
     */
    public static final int CACHE_SIZE = (int) (1 * Math.pow(2, 20)); // in MiB
    /**
     *
     */
    public static final int MEM_ACCESS_TIME = 5; // Delay bij cachemis

    public static final int CACHE_ADDRESSES = CACHE_SIZE/WORD_SIZE;
    private CacheType currentCacheType;
    /**
     *
     */
    protected File traceFile;
    private Cache cache;
    /**
     *
     */
    protected long clock;

    /**
     *
     */
    public static enum CacheType {

        /**
         * 
         */
        Plain,
        /**
         *
         */
        LinearPrefetch,

        ScalablePrefetch,
        PlainVictimCache,
        LinearPrefetch_PlainVictimCache,
        ScalablePrefetch_PlainVictimCache
    };

    /**
     *
     */
    public Simulator() {
        System.out.println("WORD SIZE: " + WORD_SIZE);
        System.out.println("CACHE_SIZE: " + CACHE_SIZE);
        System.out.println("ADDRESSES: " + CACHE_SIZE / WORD_SIZE);
        System.out.println("MEM_ACCESS_TIME: " + MEM_ACCESS_TIME);
        currentCacheType = CacheType.Plain;
        _initCacheType();
    }

    /**
     *
     * @param parseInt
     * @return
     */
    public boolean memoryAccess(long parseInt) {
        clock++;
        return cache.access(parseInt);
    }

    /**
     * 
     * @return
     */
    public Cache getCache() {
        return cache;
    }

    /**
     *
     * @param cacheType
     */
    public void setCacheType(CacheType cacheType) {
        currentCacheType = cacheType;
        resetSimulator();
    }

    public CacheType getCacheType() {
        return currentCacheType;
    }

    private void _initCacheType() {
        if (currentCacheType.equals(CacheType.Plain)) {
            cache = new DirectMappedCache(null, null);
        } else if(currentCacheType.equals(CacheType.LinearPrefetch)) {
            cache = new DirectMappedCache(new LinearPrefetch(1), null);
        } else if(currentCacheType.equals(CacheType.ScalablePrefetch)) {
            cache = new DirectMappedCache(new ScalablePrefetch(), null);
        } else if(currentCacheType.equals(CacheType.LinearPrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(new LinearPrefetch(1), new PlainVictimCache(10));
        } else if(currentCacheType.equals(CacheType.ScalablePrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(new ScalablePrefetch(), new PlainVictimCache(10));
        }
    }

    /**
     *
     */
    public void resetSimulator() {
        _initCacheType();
    }

    /**
     * Get the value of traceFile
     *
     * @return the value of traceFile
     */
    public File getTraceFile() {
        return traceFile;
    }

    /**
     * Set the value of traceFile
     *
     * @param traceFile new value of traceFile
     */
    public void setTraceFile(File traceFile) {
        this.traceFile = traceFile;
    }

    /**
     * Get the value of clock
     *
     * @return the value of clock
     */
    public long getClock() {
        return clock;
    }
}
