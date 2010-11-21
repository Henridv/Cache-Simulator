
package simulator.prefetchers;

/**
 *
 * @author Ruben Verhack
 */
public class ScalablePrefetch extends LinearPrefetch {

    /**
     * 
     */
    public ScalablePrefetch() {
        super(1);
    }

    /**
     *
     */
    @Override
    public void actionOnHit() {
        setNumberOfBlocks(getNumberOfBlocks()+1);
    }

    /**
     *
     */
    @Override
    public void actionOnMiss() {
        setNumberOfBlocks(1);
    }

    @Override
    public String toString() {
        return "ScalablePrefetch";
    }







}
