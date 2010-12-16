package simulator;

import java.io.File;
import simulator.parts.AssocCache;
import simulator.parts.Cache;
import simulator.parts.DirectMappedCache;
import simulator.prefetchers.LinearPrefetch;
import simulator.prefetchers.ScalablePrefetch;
import simulator.victimcaches.CountingPredictor;
import simulator.victimcaches.PlainVictimCache;
import simulator.victimcaches.TracePredictor;

/**
 * Dit is de hoofdklasse van het project. Dit stelt de simulator voor. Dit wordt opgeroepen
 * door TraceReadTask.
 * 
 * @author Ruben Verhack
 */
public class Simulator {

    public int blockSize = 8 * 4;
    public int cacheSize; // in MiB
    public int cacheAddresses;
    private CacheType currentCacheType;
    protected File traceFile;
    private Cache cache;
    protected int prefetchOffset;
    protected int victimSize;
    private SimulatorView simulatorView;
    protected int ways;

    /**
     * Get the value of ways
     *
     * @return the value of ways
     */
    public int getWays() {
        return ways;
    }

    /**
     * Set the value of ways
     *
     * @param ways new value of ways
     */
    public void setWays(int ways) {
        this.ways = ways;
    }

    public int getCacheAddresses() {
        return cacheAddresses;
    }

    public void setCacheAddresses(int cache_addresses) {
        this.cacheAddresses = cache_addresses;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cache_size) {
        this.cacheSize = cache_size;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int word_size) {
        this.blockSize = word_size;
    }

    /**
     * Get the value of victimSize
     *
     * @return the value of victimSize
     */
    public int getVictimSize() {
        return victimSize;
    }

    /**
     * Set the value of victimSize
     *
     * @param victimSize new value of victimSize
     */
    public void setVictimSize(int victimSize) {
        this.victimSize = victimSize;
    }

    /**
     * Get the value of prefetchOffset
     *
     * @return the value of prefetchOffset
     */
    public int getPrefetchOffset() {
        return prefetchOffset;
    }

    /**
     * Set the value of prefetchOffset
     *
     * @param prefetchOffset new value of prefetchOffset
     */
    public void setPrefetchOffset(int prefetchOffset) {
        this.prefetchOffset = prefetchOffset;
    }

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
        ScalablePrefetch_PlainVictimCache,
        Assoc,
        Assoc_Victim,
        Assoc_Prefetch_Victim,
        AssocCounter,
        AssocTrace,
        AssocCounter_ScalablePrefetch,
        AssocTrace_ScalablePrefetch
    };

    /**
     *
     */
    public Simulator() {
        System.out.println("WORD SIZE: " + blockSize);
        System.out.println("CACHE_SIZE: " + cacheSize);
        System.out.println("ADDRESSES: " + cacheSize / blockSize);
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

    public boolean memoryAccess(long parseInt, long programCounter) {
        return cache.access(parseInt, programCounter);
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
        simulatorView = (SimulatorView) SimulatorApp.getApplication().getSimulatorView();
        cacheSize = (int) (simulatorView.getCacheSize() * Math.pow(2, 10));
        //cacheSize = (int) Math.pow(2, 10);
        cacheAddresses = cacheSize / blockSize;
        victimSize = simulatorView.getVictimCacheSize();
        prefetchOffset = simulatorView.getPrefetchOffset();

        ways = simulatorView.getAssocSetsNumber();

        if (currentCacheType.equals(CacheType.Plain)) {
            cache = new DirectMappedCache(cacheAddresses, null, null, false);
        } else if (currentCacheType.equals(CacheType.LinearPrefetch)) {
            cache = new DirectMappedCache(cacheAddresses, new LinearPrefetch(prefetchOffset), null, false);
        } else if (currentCacheType.equals(CacheType.ScalablePrefetch)) {
            cache = new DirectMappedCache(cacheAddresses, new ScalablePrefetch(), null, false);
        } else if (currentCacheType.equals(CacheType.LinearPrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(cacheAddresses, new LinearPrefetch(prefetchOffset), new PlainVictimCache(victimSize), false);
        } else if (currentCacheType.equals(CacheType.ScalablePrefetch_PlainVictimCache)) {
            cache = new DirectMappedCache(cacheAddresses, new ScalablePrefetch(), new PlainVictimCache(victimSize), false);
        } else if (currentCacheType.equals(CacheType.Assoc)) {
            cache = new AssocCache(cacheSize, ways);
        } else if (currentCacheType.equals(CacheType.Assoc_Victim)) {
            cache = new AssocCache(cacheSize, ways, new PlainVictimCache(victimSize), null);
        } else if (currentCacheType.equals(CacheType.Assoc_Prefetch_Victim)) {
            cache = new AssocCache(cacheSize, ways, new PlainVictimCache(victimSize), new ScalablePrefetch());
        } else if (currentCacheType.equals(CacheType.AssocCounter)) {
            cache = new AssocCache(cacheSize, ways, new CountingPredictor(), null);
        } else if (currentCacheType.equals(CacheType.AssocTrace)) {
            cache = new AssocCache(cacheSize, ways, new TracePredictor(), null);
        } else if (currentCacheType.equals(CacheType.AssocCounter_ScalablePrefetch)) {
            cache = new AssocCache(cacheSize, ways, new CountingPredictor(), new ScalablePrefetch());
        } else if (currentCacheType.equals(CacheType.AssocTrace_ScalablePrefetch)) {
            cache = new AssocCache(cacheSize, ways, new TracePredictor(), new ScalablePrefetch());
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
