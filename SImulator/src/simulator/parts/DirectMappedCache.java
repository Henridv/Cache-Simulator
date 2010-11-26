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

    /**
     *
     *
     * @param prefetcher prefetch 
     */
    public DirectMappedCache(int cacheSize, Prefetcher prefetcher, PlainVictimCache victimCache) {
        this.simulator = SimulatorApp.getApplication().getSimulator();
        this.cache = new long[cacheSize];
        this.prefetcher = prefetcher;
        this.victimCache = victimCache;
        this.simulator = SimulatorApp.getApplication().getSimulator();
    }

    /**
     * Simuleer een geheugentoegang en tel het aantal hits en misses in de cache
     * Eventueel gebruikmakend van prefetch en victimcache
     * @param address
     * @return
     */
    @Override
    public boolean access(final long address) {
        boolean hit;
        final int cacheAddress = (int) ((address / simulator.getWordSize()) % simulator.getCacheAddresses());
        final long memAddress = (address / simulator.getWordSize());

        // Zoek in cache
        if (cache[cacheAddress] == memAddress) {
            hit = true;
            if (prefetcher != null) {
                prefetcher.actionOnHit();
            }
            hits++;
        } else // Indien niet in cache
        {
            // Zoek in vitim cache als er victim cache is
            if (victimCache != null && victimCache.contains(memAddress)) {

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
                }
                hit = false;
                
                if (prefetcher != null) {
                    prefetcher.actionOnMiss();
                }
                misses++;
            }
        }

        return hit;
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
