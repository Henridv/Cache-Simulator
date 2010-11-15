package simulator.parts;

import java.util.ArrayList;
import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.prefetchers.Prefetcher;

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

    /**
     *
     */
    public DirectMappedCache() {
        cache = new int[Simulator.CACHE_SIZE / Simulator.WORD_SIZE];
        onItsWayList = new ArrayList<Integer>();
        prefetcher = null;
    }

    /**
     *
     * @param prefetcher
     */
    public DirectMappedCache(Prefetcher prefetcher) {
        cache = new int[Simulator.CACHE_SIZE / Simulator.WORD_SIZE];
        onItsWayList = new ArrayList<Integer>();
        this.prefetcher = prefetcher;

    }

    /**
     * 
     * @param address
     * @return
     */
    @Override
    public boolean access(final int address) {

        simulator = SimulatorApp.getApplication().getSimulator();
        boolean hit;
        final int cacheAddress = (address / Simulator.WORD_SIZE) % Simulator.CACHE_SIZE;
        final int memAddress = (address / Simulator.WORD_SIZE);

        if (cache[cacheAddress] == memAddress || onItsWayList.contains(memAddress)) {
            hit = true;
            if(prefetcher != null) {
                prefetcher.actionOnHit();
            }
            hits++;
        } else {
            final long time = simulator.getClock();
            Thread delayThread = new Thread(new Runnable() {

                public void run() {
                    onItsWayList.add(memAddress);
                    while (simulator.getClock() < time + Simulator.MEM_ACCESS_TIME) {
                    }
                    cache[cacheAddress] = memAddress;
                    // Call prefetcher if necessary
                    if(prefetcher != null) {
                        prefetcher.prefetchMemory(cache, memAddress);
                    }
                    onItsWayList.remove((Integer) memAddress);
                }
            });
            hit = false;
            if(prefetcher != null) {
                prefetcher.actionOnMiss();
            }
            misses++;
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
}
