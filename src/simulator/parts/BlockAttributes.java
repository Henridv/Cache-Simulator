/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simulator.parts;

/**
 *
 * @author Ruben Verhack <ruben@ninetynine.be>
 */
public class BlockAttributes {

    protected long block;
    protected boolean valid;
    protected boolean prefetched;

    public BlockAttributes(long block, boolean valid, boolean prefetched) {
        this.block = block;
        this.valid = valid;
        this.prefetched = prefetched;
    }

    

    /**
     * Get the value of prefetched
     *
     * @return the value of prefetched
     */
    public boolean isPrefetched() {
        return prefetched;
    }

    /**
     * Set the value of prefetched
     *
     * @param prefetched new value of prefetched
     */
    public void setPrefetched(boolean prefetched) {
        this.prefetched = prefetched;
    }


    /**
     * Get the value of valid
     *
     * @return the value of valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Set the value of valid
     *
     * @param valid new value of valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }


    /**
     * Get the value of block
     *
     * @return the value of block
     */
    public long getBlock() {
        return block;
    }

    /**
     * Set the value of block
     *
     * @param block new value of block
     */
    public void setBlock(long block) {
        this.block = block;
    }


}
