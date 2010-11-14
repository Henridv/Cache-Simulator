package simulator.prefetchers;

/**
 *
 * @author Ruben Verhack
 */
public abstract class Prefetcher {

    public abstract void prefetchMemory(int[] memory, long address);

}
