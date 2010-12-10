package simulator.prefetchers;

import java.util.LinkedHashMap;
import simulator.parts.AssocCache;
import simulator.victimcaches.IDeadblockPredictor;

/**
 *
 * @author Ruben Verhack <ruben@ninetynine.be>
 */
public class SelectivePrefetch {

    protected IDeadblockPredictor predictor;

    public SelectivePrefetch(IDeadblockPredictor predictor) {
        this.predictor = predictor;
    }

    public void prefetchMemory(AssocCache assocCache, long memAddress) {

        int set = (int) ((memAddress >>> assocCache.getOffset()) % assocCache.getSets());
        long tag = (memAddress >>> assocCache.getOffset()) >>> assocCache.getSetIndexBits();
        LinkedHashMap<Long, Boolean>[] cache = assocCache.getCache();

//        boolean dead = predictor.access(tag);
//        if (dead) {
//            System.out.println("DEAD");
//        }
//
//        if (cache[set].containsKey(tag) && cache[set].get(tag)) {
//        } else {
//        }

    }

    public void actionOnHit() {
        //
    }

    public void actionOnMiss() {
        //
    }

    /**
     * Get the value of predictor
     *
     * @return the value of predictor
     */
    public IDeadblockPredictor getPredictor() {
        return predictor;
    }

    /**
     * Set the value of predictor
     *
     * @param predictor new value of predictor
     */
    public void setPredictor(IDeadblockPredictor predictor) {
        this.predictor = predictor;
    }
}
