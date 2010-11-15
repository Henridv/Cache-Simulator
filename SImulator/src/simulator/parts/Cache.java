package simulator.parts;

/**
 *
 * @author Ruben Verhack
 */
public abstract class Cache {

    /**
     *
     */
    protected long misses;
    /**
     * 
     */
    protected long hits;



    /**
     * Get the value of hits
     *
     * @return the value of hits
     */
    public long getHits() {
        return hits;
    }

    /**
     * Set the value of hits
     *
     * @param hits new value of hits
     */
    public void setHits(long hits) {
        this.hits = hits;
    }


    /**
     * Get the value of misses
     *
     * @return the value of misses
     */
    public long getMisses() {
        return misses;
    }

    /**
     * Set the value of misses
     *
     * @param misses new value of misses
     */
    public void setMisses(long misses) {
        this.misses = misses;
    }

    /**
     *
     * @return
     */
    public float getMissRate() {
        return (float)misses/((float)hits+(float)misses);
    }

    /**
     *
     * @return
     */
    public float getHitRate() {
        return (float)hits/((float)hits+(float)misses);
    }


    // True if hit, false if miss
    /**
     *
     * @param address
     * @return
     */
    public abstract boolean access(int address);

}
