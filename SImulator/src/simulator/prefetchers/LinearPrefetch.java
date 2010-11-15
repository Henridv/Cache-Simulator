package simulator.prefetchers;

import simulator.Simulator;

/**
 *
 * @author Ruben Verhack
 */
public class LinearPrefetch extends Prefetcher {

    /**
     *
     */
    protected int numberOfBlocks;

    /**
     *
     * @param numberOfBlocks
     */
    public LinearPrefetch(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     * Get the value of numberOfBlocks
     *
     * @return the value of numberOfBlocks
     */
    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    /**
     * Set the value of numberOfBlocks
     *
     * @param numberOfBlocks new value of numberOfBlocks
     */
    public void setNumberOfBlocks(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
    }

    /**
     *
     * @param memory
     * @param memAddress
     */
    public void prefetchMemory(int[] memory, int memAddress) {
        for (int i = 0; i < numberOfBlocks; i++) {
            memory[(memAddress + i) % Simulator.CACHE_SIZE] = memAddress;
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
}
