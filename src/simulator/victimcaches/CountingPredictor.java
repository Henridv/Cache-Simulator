package simulator.victimcaches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author henri
 */
public class CountingPredictor implements IDeadblockPredictor {

    protected HashMap<Long, Long> pcs;
    protected LinkedHashMap<CountingPredictorEntry, Long> history;
    protected HashMap<CountingPredictorEntry, Long> counter;
    protected ArrayList<Long> deadBlocks;

	private final int historyEntries = (int)Math.pow(2,7);

    public CountingPredictor() {
        history = new LinkedHashMap<CountingPredictorEntry, Long>(historyEntries, 1, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<CountingPredictorEntry, Long> eldest) {
				return size() > historyEntries;
			}
		};
		// Fill the with invalid blocks
		for (int i = 0; i < historyEntries; i++) {
			this.history.put(new CountingPredictorEntry(0L, 0L), 0L);
		}
        counter = new HashMap<CountingPredictorEntry, Long>();
        pcs = new HashMap<Long, Long>();
        deadBlocks = new ArrayList<Long>();

    }

    public boolean access(long cacheBlock, long programCounter) {
        // Check if there is already a init_pc for this cacheblock
        if (pcs.get(cacheBlock) == null) {
            pcs.put(cacheBlock, programCounter);
        }

        CountingPredictorEntry entry = new CountingPredictorEntry(cacheBlock, pcs.get(cacheBlock));

        if (history.get(entry) == null) {
            // cacheBlock has never been evicted with this pc
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
            counter.put(entry, count);
            if (count > 0L) {
                return false;
            } else {
                if (!deadBlocks.contains(cacheBlock)) {
                    deadBlocks.add(cacheBlock);
                }
                return true;
            }
        }

    }

    public void evict(long cacheBlock) {

        if (pcs.get(cacheBlock) != null) {
            CountingPredictorEntry entry = new CountingPredictorEntry(cacheBlock, pcs.get(cacheBlock));

            // If the cacheBlock is evicted for the first time
            if (history.get(entry) == null) {
                pcs.remove(cacheBlock);
                history.put(entry, counter.get(entry));
            } else {
                deadBlocks.remove(cacheBlock);  // Verwijderen wnr het als dode blok vervangen wordt
                pcs.remove(cacheBlock);         // uit huidige programcounters verwijderen
                counter.put(entry, history.get(entry));
            }
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

    @Override
    public String toString() {
        return "CountingPredictor";
    }
}
