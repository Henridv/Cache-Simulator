package simulator.prefetchers;

import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.victimcaches.PlainVictimCache;

/**
 *
 * @author Ruben Verhack
 */
public class LinearPrefetch extends Prefetcher {

    /**
     *
     */
    protected long numberOfBlocks;

    /**
     *
     * @param numberOfBlocks
     */
    public LinearPrefetch(long numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     * Get the value of numberOfBlocks
     *
     * @return the value of numberOfBlocks
     */
    public long getNumberOfBlocks() {
        return numberOfBlocks;
    }

    /**
     * Set the value of numberOfBlocks
     *
     * @param numberOfBlocks new value of numberOfBlocks
     */
    public void setNumberOfBlocks(long numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     *
     * @param memory
     * @param memAddress
     */
    public void prefetchMemory(long[] memory, long memAddress, PlainVictimCache victimCache) {
        Simulator simulator = SimulatorApp.getApplication().getSimulator();
        int index;
        for (int i = 0; i < numberOfBlocks; i++) {
            index = (int) (((memAddress) + i) % simulator.getCacheAddresses());
            if (victimCache != null) {
                victimCache.add(index);
            }
            memory[index] = memAddress + i;
        }
    }

    // Simpele cache, houdt geen rekening met hits en misses
    /**
     *
     */
    @Override
    public void actionOnHit() {
    }

    /**
     * 
     */
    @Override
    public void actionOnMiss() {
    }

    @Override
    public String toString() {
        return "LinearPrefetch";
    }
}
