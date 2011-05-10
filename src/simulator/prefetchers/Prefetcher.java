package simulator.prefetchers;

import simulator.victimcaches.PlainVictimCache;

/**
 *
 * @author Ruben Verhack
 */
public abstract class Prefetcher {

    /**
     *
     * @param memory
     * @param memAddress
     */
    public abstract void prefetchMemory(long[] memory, long memAddress, PlainVictimCache victimCache);
    /**
     * 
     */
    public abstract void actionOnHit();
    /**
     *
     */
    public abstract void actionOnMiss();

}
