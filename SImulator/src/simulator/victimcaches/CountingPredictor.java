package simulator.victimcaches;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author henri
 */
public class CountingPredictor implements IDeadblockPredictor {

	protected HashMap<Long, Long> pcs;
	protected HashMap<ArrayList<Long>, Long> history;
	protected HashMap<ArrayList<Long>, Long> counter;
	protected ArrayList<Long> deadBlocks;

	public CountingPredictor() {
		history = new HashMap<ArrayList<Long>, Long>();
		counter = new HashMap<ArrayList<Long>, Long>();
		pcs = new HashMap<Long, Long>();
		deadBlocks = new ArrayList<Long>();
	}

	public boolean access(long cacheBlock, long programCounter) {
		if (pcs.get(cacheBlock) == null)
			pcs.put(cacheBlock, programCounter);

		ArrayList<Long> pair = new ArrayList<Long>();
		pair.add(cacheBlock);
		pair.add(pcs.get(cacheBlock));

		if (history.get(pair) == null) {
			// cacheBlock has never been evicted
			Long count = counter.get(pair);
			if (count == null) {
				counter.put(pair, 1L);
			} else {
				counter.put(pair, count + 1);
			}
			return false;
		} else {
			// cacheBlock is referenced the 2nd or more time
			Long count = counter.get(pair);
			count--;

			// cacheBlock is predicted dead after this reference
			if (count != 0L) {
				counter.put(pair, count);
				deadBlocks.remove(cacheBlock);
				return false;
			} else {
				counter.put(pair, 0L);
				deadBlocks.add(cacheBlock);
				return true;
			}
		}
	}

	public void evict(long cacheBlock) {
		ArrayList<Long> pair = new ArrayList<Long>();
		pair.add(cacheBlock);
		pair.add(pcs.get(cacheBlock));
		if (history.get(pair) == null) {
			pcs.remove(cacheBlock);
			history.put(pair, counter.get(pair));
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
