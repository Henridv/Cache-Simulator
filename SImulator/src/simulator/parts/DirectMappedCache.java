package simulator.parts;

import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.prefetchers.Prefetcher;
import simulator.victimcaches.PlainVictimCache;

/**
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
     * @param prefetcher
     */
    public DirectMappedCache(Prefetcher prefetcher, PlainVictimCache victimCache) {
        cache = new long[Simulator.CACHE_ADDRESSES];
        this.prefetcher = prefetcher;
        this.victimCache = victimCache;
    }

    /**
     * Simuleer een geheugentoegang en tel het aantal hits en misses in de cache
     * Eventueel gebruikmakend van prefetch en victimcache
     * @param address
     * @return
     */
    @Override
    public boolean access(final long address) {

        simulator = SimulatorApp.getApplication().getSimulator();
        boolean hit;
        final int cacheAddress = (int) ((address / Simulator.WORD_SIZE) % Simulator.CACHE_ADDRESSES);
        final long memAddress = (address / Simulator.WORD_SIZE);

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
                // TODO: blijkbaar komt hij hier nooit

                victimCache.switchAddresses(cache[cacheAddress], memAddress);
                cache[cacheAddress] = memAddress;

                hit = true;
                hits++;

            } else // Niet in victim cache en niet in cache => miss
            {

                if (victimCache != null) {
                    victimCache.add(memAddress);
                }
                cache[cacheAddress] = memAddress;
                // Call prefetcher if necessary
                if (prefetcher != null) {
                    prefetcher.prefetchMemory(cache, memAddress);
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
