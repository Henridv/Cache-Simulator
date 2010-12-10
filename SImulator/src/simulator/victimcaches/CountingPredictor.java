package simulator.victimcaches;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author henri
 */
public class CountingPredictor implements IDeadblockPredictor {

    protected HashMap<Long, Long> pcs;
    protected HashMap<CountingPredictorEntry, Long> history;
    protected HashMap<CountingPredictorEntry, Long> counter;
    protected ArrayList<Long> deadBlocks;

    public CountingPredictor() {
        history = new HashMap<CountingPredictorEntry, Long>();
        counter = new HashMap<CountingPredictorEntry, Long>();
        pcs = new HashMap<Long, Long>();
        deadBlocks = new ArrayList<Long>();

    }

    public boolean access(long cacheBlock, long programCounter) {
        if (pcs.get(cacheBlock) == null) {
            pcs.put(cacheBlock, programCounter);
        }


        CountingPredictorEntry entry = new CountingPredictorEntry(cacheBlock, pcs.get(cacheBlock));

        if (history.get(entry) == null) {
            // cacheBlock has never been evicted
            Long count = counter.get(entry);
            if (count == null) {
                counter.put(entry, 1L);
            } else {
                counter.put(entry, count + 1);
            }
            return false;
        } else {
            // cacheBlock is referenced the 2nd or more time
            Long count = counter.get(entry);
            count--;

            // cacheBlock is predicted dead after this reference
            if (count != 0L) {
                counter.put(entry, count);
                return false;
            } else {
                counter.put(entry, 0L);
                if (!deadBlocks.contains(cacheBlock)) {
                    deadBlocks.add(cacheBlock);
                }
                return true;
            }
        }

    }

    public void evict(long cacheBlock) {

        CountingPredictorEntry entry = new CountingPredictorEntry(cacheBlock, pcs.get(cacheBlock));

        // If the cacheBlock is evicted for the first time
        if (history.get(entry) == null) {
            pcs.remove(cacheBlock);
            history.put(entry, counter.get(entry));
        } else {
            deadBlocks.remove(cacheBlock);
            counter.put(entry, history.get(entry));
        }
    }

    /**
     * Return TRUE when cacheBlock is dead
     * FALSE when cacheBlock is not dead or when cacheBlock has not been referenced
     * @param cacheBlock
     * @return
     */
    public boolean isDead(long cacheBlock) {
        return deadBlocks.contains(cacheBlock);
    }
}
