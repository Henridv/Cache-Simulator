package simulator.prefetchers;

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
    public abstract void prefetchMemory(long[] memory, long memAddress);
    /**
     * 
     */
    public abstract void actionOnHit();
    /**
     *
     */
    public abstract void actionOnMiss();

}
