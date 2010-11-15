package simulator.victimcaches;

import java.util.LinkedList;

/**
 *
 * @author Ruben Verhack
 */
public class PlainVictimCache {

    protected LinkedList<Integer> victimCache;
    protected int size;

    public PlainVictimCache(int size) {
        this.size = size;
        this.victimCache = new LinkedList<Integer>();
    }

    public boolean contains(int memAddress) {

        return victimCache.contains((Integer) memAddress);
    }

    public void add(int memAddress) {
        victimCache.addFirst(memAddress);
        if(size > victimCache.size()) {
            victimCache.removeLast();
        }
    }

    public boolean switchAddresses(int oldMemAddress, int newMemAddress) {
        victimCache.addFirst(newMemAddress);
        return victimCache.remove((Integer) oldMemAddress);
    }

    /**
     * Get the value of size
     *
     * @return the value of size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the value of size
     *
     * @param size new value of size
     */
    public void setSize(int size) {
        this.size = size;
    }
}
