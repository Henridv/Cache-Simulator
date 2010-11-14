package simulator;

import java.io.File;
import simulator.parts.Cache;
import simulator.parts.DirectMappedCache;

/**
 *
 * @author Ruben Verhack
 */
public class Simulator {

    public static final int WORD_SIZE = 64;
    public static final int CACHE_SIZE = (int) (2 * Math.pow(2, 20)); // in MiB
    public static final int MEM_ACCESS_TIME = 5; // Delay bij cachemis
    private CacheType currentCacheType;
    protected File traceFile;
    private Cache cache;
    protected long clock;

    public static enum CacheType {

        DirectMappedCache
    };

    public Simulator() {
        System.out.println("WORD SIZE: " + WORD_SIZE);
        System.out.println("CACHE_SIZE: " + CACHE_SIZE);
        System.out.println("ADDRESSES: " + CACHE_SIZE / WORD_SIZE);
        System.out.println("MEM_ACCESS_TIME: " + MEM_ACCESS_TIME);
        currentCacheType = CacheType.DirectMappedCache;
        _initCacheType();
    }

    public boolean memoryAccess(int parseInt) {
        clock++;
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
