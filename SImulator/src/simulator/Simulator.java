package simulator;

import java.io.File;
import simulator.parts.Cache;
import simulator.parts.DirectMappedCache;
import simulator.prefetchers.LinearPrefetch;
import simulator.prefetchers.ScalablePrefetch;
import simulator.victimcaches.PlainVictimCache;

/**
 * Dit is de hoofdklasse van het project. Dit stelt de simulator voor. Dit wordt opgeroepen
 * door TraceReadTask.
 * 
 * @author Ruben Verhack
 */
public class Simulator {

    public static final int WORD_SIZE = 64;
    public static final int CACHE_SIZE = (int) (1 * Math.pow(2, 20)); // in MiB
    public static final int CACHE_ADDRESSES = CACHE_SIZE/WORD_SIZE;
    private CacheType currentCacheType;
    protected File traceFile;
    private Cache cache;

    /**
     * Dit zijn de configuraties die mogelijk zijn. Als je hier één toevoegt
     * dan moet je ook simulator.GUI.CacheTypeModel aanpassen (zodat je de dropdown
     * kunt gebruiken), ook moet de if constructie bij _initCacheType() aangevuld
     * worden. (Kweet niet de beste stijl)
     */
    public static enum CacheType {

        Plain,
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
        currentCacheType = CacheType.Plain;
        _initCacheType();
    }

    /**
     *
     * @param parseInt
     * @return
     */
    public boolean memoryAccess(long parseInt) {
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

    /**
     * Initialiseer de cache
     */
    private void _initCacheType() {
        if (currentCacheType.equals(CacheType.Plain)) {
            cache = new DirectMappedCache(null, null);
        } else if(currentCacheType.equals(CacheType.LinearPrefetch)) {
            cache = new DirectMappedCache(new LinearPrefetch(3), null);
        } else if(currentCacheType.equals(CacheType.ScalablePrefetch)) {
            cache = new DirectMappedCache(new ScalablePrefetch(), null);
        } else if(currentCacheType.equals(CacheType.LinearPrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(new LinearPrefetch(3), new PlainVictimCache(10));
        } else if(currentCacheType.equals(CacheType.ScalablePrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(new ScalablePrefetch(), new PlainVictimCache(10));
        }
    }

    /**
     * Reset
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

}
