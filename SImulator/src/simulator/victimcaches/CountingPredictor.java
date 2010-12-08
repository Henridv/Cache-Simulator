package simulator.victimcaches;

import java.util.HashMap;

/**
 *
 * @author henri
 */
public class CountingPredictor implements IDeadblockPredictor {

	protected HashMap<Long, Long> history;
	protected HashMap<Long, Long> counter;

	public CountingPredictor() {
		history = new HashMap<Long, Long>();
		counter = new HashMap<Long, Long>();
	}

	public boolean access(long cacheBlock) {
		if (history.get(cacheBlock) == null) {
			// cacheBlock has never been evicted
			Long count = counter.get(cacheBlock);
			if (count == null) {
				counter.put(cacheBlock, 1L);
			} else {
				counter.put(cacheBlock, count + 1);
			}
			return false;
		} else {
			// cacheBlock is referenced the 2nd or more time
			Long count = counter.get(cacheBlock);
			count--;

			// cacheBlock is predicted dead after this reference
			if (count == 0) {
				// reset counter
				counter.put(cacheBlock, history.get(cacheBlock));
				return true;
			} else {
				counter.put(cacheBlock, count);
				return false;
			}
		}
	}

	public void evict(long cacheBlock) {
		if (history.get(cacheBlock) != null)
			history.put(cacheBlock, counter.get(cacheBlock));
	}

	/**
	 * Return TRUE when cacheBlock is dead
	 * FALSE when cacheBlock is not dead or when cacheBlock has not been referenced
	 * @param cacheBlock
	 * @return
	 */
	public boolean isDead(long cacheBlock) {
		if (history.get(cacheBlock) == null) {
			return false;
		} else if (counter.get(cacheBlock) == null) {
			return false;
		} else {
			return counter.get(cacheBlock) == history.get(cacheBlock);
		}
	}
}
