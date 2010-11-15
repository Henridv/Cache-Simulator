package simulator.parts;

import java.util.ArrayList;
import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.prefetchers.Prefetcher;
import simulator.victimcaches.PlainVictimCache;

/**
 *
 * @author Ruben Verhack
 */
public class DirectMappedCache extends Cache {

    private int[] cache;
    private Simulator simulator;
    /**
     *
     */
    protected ArrayList<Integer> onItsWayList;
    /**
     *
     */
    protected Prefetcher prefetcher;
    protected PlainVictimCache victimCache;

    /**
     *
     */
    public DirectMappedCache() {
        cache = new int[Simulator.CACHE_SIZE / Simulator.WORD_SIZE];
        onItsWayList = new ArrayList<Integer>();
        prefetcher = null;
        victimCache = new PlainVictimCache(10);
    }

    /**
     *
     * @param prefetcher
     */
    public DirectMappedCache(Prefetcher prefetcher) {
        cache = new int[Simulator.CACHE_SIZE / Simulator.WORD_SIZE];
        onItsWayList = new ArrayList<Integer>();
        this.prefetcher = prefetcher;
        victimCache = new PlainVictimCache(10);
    }

    /**
     * Simuleer een geheugentoegang en tel het aantal hits en misses in de cache
     * Eventueel gebruikmakend van prefetch en victimcache
     * @param address
     * @return
     */
    @Override
    public boolean access(final int address) {

        simulator = SimulatorApp.getApplication().getSimulator();
        boolean hit;
        final int cacheAddress = (address / Simulator.WORD_SIZE) % Simulator.CACHE_SIZE;
        final int memAddress = (address / Simulator.WORD_SIZE);

        // Zoek in cache
        if (cache[cacheAddress] == memAddress || onItsWayList.contains(memAddress)) {
            hit = true;
            if (prefetcher != null) {
                prefetcher.actionOnHit();
            }
            hits++;
        } 
        else  // Indien niet in cache
        {
            // Zoek in vitim cache als er victim cache is
            if (victimCache != null && victimCache.contains(memAddress)) {

                victimCache.switchAddresses(cache[cacheAddress], memAddress);
                cache[cacheAddress] = memAddress;

                hit = true;
                hits++;

            } 
            else // Niet in victim cache en niet in cache => miss
            {
                final long time = simulator.getClock();
                Thread delayThread = new Thread(new Runnable() {

                    public void run() {
                        onItsWayList.add(memAddress);
                        while (simulator.getClock() < time + Simulator.MEM_ACCESS_TIME) {
                        }
                        if (victimCache != null) {
                            victimCache.add(memAddress);
                        }
                        cache[cacheAddress] = memAddress;
                        // Call prefetcher if necessary
                        if (prefetcher != null) {
                            prefetcher.prefetchMemory(cache, memAddress);
                        }
                        onItsWayList.remove((Integer) memAddress);
                    }
                });
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
}
