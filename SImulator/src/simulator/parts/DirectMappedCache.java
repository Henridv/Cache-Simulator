
package simulator.parts;

import simulator.Simulator;

/**
 *
 * @author ruben
 */
public class DirectMappedCache extends Cache{

    private int[] cache;
    

    public DirectMappedCache() {

        cache = new int[Simulator.CACHE_SIZE/Simulator.WORD_SIZE];
    }

    @Override
    public boolean access(int address) {
        boolean hit;

        if(cache[(int)(address/Simulator.CACHE_SIZE)] == address) {
            hit = true;
            hits++;
        } else {
            cache[(int)(address/Simulator.CACHE_SIZE)] = address;
            hit = false;
            misses++;
        }

        return hit;
    }

}
