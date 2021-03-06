package simulator.parts;

import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.prefetchers.Prefetcher;
import simulator.victimcaches.PlainVictimCache;

/**
 * Dit is de hoofdcache van het project. Dit is een standaard direct mapped cache
 * dat als er een prefetch en/of victim cache wordt meegegeven in de constructor
 * hier dan ook rekening mee houdt.
 * 
 * @author Ruben Verhack
 */
public class DirectMappedCache extends Cache {

    private long[] cache;
    private Simulator simulator;
    /**
     *
     */
    protected Prefetcher prefetcher;
    protected PlainVictimCache victimCache;
    protected boolean isL1Cache;

    /**
     *
     *
     * @param prefetcher prefetch 
     */
    public DirectMappedCache(int cacheSize, Prefetcher prefetcher, PlainVictimCache victimCache, boolean isL1Cache) {
        this.simulator = SimulatorApp.getApplication().getSimulator();
        this.cache = new long[cacheSize];
        this.prefetcher = prefetcher;
        this.victimCache = victimCache;
        this.simulator = SimulatorApp.getApplication().getSimulator();
        this.hits = 0;
        this.misses = 0;
        this.isL1Cache = isL1Cache;
        for (int i = 0; i < cacheSize; i++) {
            this.cache[i] = 0;
        }
    }

    /**
     * Simuleer een geheugentoegang en tel het aantal hits en misses in de cache
     * Eventueel gebruikmakend van prefetch en victimcache
     * @param address
     * @return
     */
    @Override
    public boolean access(final long address) {
        final int cacheAddress;
        boolean hit;
//        System.out.println(address + " "+ simulator.getBlockSize() + " " + (address / simulator.getBlockSize()) + " " + simulator.getCacheAddresses());
//        System.out.println((address / simulator.getBlockSize() + " " +((long) ((address / simulator.getBlockSize())))) +" " +(((long) ((address / simulator.getBlockSize()))) % simulator.getCacheAddresses()) + " "+cacheAddress);
//        System.out.println(Long.MAX_VALUE);
        if (isL1Cache) {
            cacheAddress = (int) (((long) ((address / simulator.getBlockSize()))) % (cache.length));
        } else {
            cacheAddress = (int) (((long) ((address / simulator.getBlockSize()))) % simulator.getCacheAddresses());
        }

        final long memAddress = (address / simulator.getBlockSize());

        // Zoek in cache
        if (cache[cacheAddress] == memAddress) {
            hit = true;
            if (prefetcher != null) {
                prefetcher.actionOnHit();
            }
            hits++;
        } else // Indien niet in cache
        if (victimCache != null && victimCache.contains(memAddress)) // Zoek in vitim cache als er victim cache is
        {
            // Indien gevonden, verwissel het dan met de entry in de cache
            victimCache.switchAddresses(cache[cacheAddress], memAddress);
            cache[cacheAddress] = memAddress;

            // Hit it
            hit = true;
            hits++;

        } else // Niet in victim cache en niet in cache => miss
        {

            // Steek wat er nu in de cache zit in de victim cache
            if (victimCache != null) {
                victimCache.add(cache[cacheAddress]);
            }

            // Haal het nieuwe uit het hoofdgeheugen en steek het in de cache
            cache[cacheAddress] = memAddress;

            // Call prefetcher if necessary,
            if (prefetcher != null) {
                prefetcher.prefetchMemory(cache, memAddress, victimCache);
                prefetcher.actionOnMiss();
            }
            hit = false;
            misses++;
//            System.out.println(address + "\n" + memAddress + " " + cacheAddress + " MISS " + misses);
        }


        return hit;
    }

    @Override
    public boolean access(long parseInt, long programCounter) {
        return access(parseInt);
    }

    /**
     * Get the value of prefetcher
     *
     * @return the value of prefetcher
     */
    public Prefetcher getPrefetcher() {
        return prefetcher;
    }

    /**
     * Set the value of prefetcher
     *
     * @param prefetcher new value of prefetcher
     */
    public void setPrefetcher(Prefetcher prefetcher) {
        this.prefetcher = prefetcher;
    }

    /**
     * Get the value of victimCache
     *
     * @return the value of victimCache
     */
    public PlainVictimCache getVictimCache() {
        return victimCache;
    }

    /**
     * Set the value of victimCache
     *
     * @param victimCache new value of victimCache
     */
    public void setVictimCache(PlainVictimCache victimCache) {
        this.victimCache = victimCache;
    }

    @Override
    public String toString() {
        if (prefetcher == null && victimCache == null) {
            return "Plain";
        } else if (prefetcher == null && victimCache != null) {
            return "" + victimCache;
        } else if (prefetcher != null && victimCache == null) {
            return "" + prefetcher;
        } else {
            return prefetcher + "_" + victimCache;
        }
    }
}
