/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simulator.victimcaches;

/**
 *
 * @author Ruben Verhack <ruben@ninetynine.be>
 */
public class CountingPredictorEntry {

    protected long cacheBlock;
    protected int set;
    protected long pc;

    public CountingPredictorEntry(long cacheBlock, long pc) {
        this.cacheBlock = cacheBlock;
        this.pc = pc;
    }

    

    /**
     * Get the value of pc
     *
     * @return the value of pc
     */
    public long getPc() {
        return pc;
    }

    /**
     * Set the value of pc
     *
     * @param pc new value of pc
     */
    public void setPc(long pc) {
        this.pc = pc;
    }


    /**
     * Get the value of set
     *
     * @return the value of set
     */
    public int getSet() {
        return set;
    }

    /**
     * Set the value of set
     *
     * @param set new value of set
     */
    public void setSet(int set) {
        this.set = set;
    }


    /**
     * Get the value of cacheBlock
     *
     * @return the value of cacheBlock
     */
    public long getCacheBlock() {
        return cacheBlock;
    }

    /**
     * Set the value of cacheBlock
     *
     * @param cacheBlock new value of cacheBlock
     */
    public void setCacheBlock(long cacheBlock) {
        this.cacheBlock = cacheBlock;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof CountingPredictorEntry) {
            CountingPredictorEntry entry2 = (CountingPredictorEntry) o;
            if(entry2.getCacheBlock() == cacheBlock && entry2.getPc() == pc && entry2.getSet() == set) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (this.cacheBlock ^ (this.cacheBlock >>> 32));
        hash = 37 * hash + this.set;
        hash = 37 * hash + (int) (this.pc ^ (this.pc >>> 32));
        return hash;
    }




}
