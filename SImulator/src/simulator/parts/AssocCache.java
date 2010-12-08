/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.parts;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.victimcaches.IDeadblockPredictor;

/**
 *
 * @author henri
 */
public class AssocCache extends Cache {

	/**
	 * Long: cache tag
	 * Boolean: TRUE => dead
	 */
	protected LinkedHashMap<Long, Boolean>[] cache;
	protected IDeadblockPredictor predictor;
	private Simulator simulator;
	private int offset;
	private int setIndex;
	private int blockSize;

	public AssocCache(int cacheSize, int sets, final IDeadblockPredictor predictor) {
		this.simulator = SimulatorApp.getApplication().getSimulator();
		this.blockSize = simulator.getWordSize();
		this.offset = (int) (Math.log(blockSize) / Math.log(2));
		this.setIndex = (int) (Math.log(sets) / Math.log(2));

		this.predictor = predictor;
		
		// number of blocks per set
		final int setSize = (cacheSize / blockSize) / sets;

		// init sets, automatic LRU
		this.cache = new LinkedHashMap[sets];
		for (int i = 0; i < sets; i++) {
			this.cache[i] = new LinkedHashMap<Long, Boolean>(setSize, 1, true) {

				@Override
				protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
					return removeEldest(size(), setSize, eldest);
				}
			};
			for (int j = 0; j < setSize; j++) {
				this.cache[i].put(0L, Boolean.FALSE);
			}
		}

	}

	@Override
	public boolean access(long address) {
		int set = (int) ((address >> offset) % (Math.pow(2, setIndex)));
		long tag = (address >> offset) >> setIndex;

		if (predictor != null) {
			boolean dead = predictor.access(tag);
			if(dead) System.out.println("DEAD");
		}
		if (cache[set].containsKey(tag) && cache[set].get(tag)) {
			hits++;
			return true;
		} else {
			misses++;
			cache[set].put(tag, Boolean.TRUE);
			return false;
		}
	}

	/**
	 * If there is a predictor for dead blocks this method will check if there are dead blocks to remove
	 * @param size
	 * @param eldest
	 * @return
	 */
	private boolean removeEldest(int size, int setSize, Map.Entry<Long, Boolean> eldest) {
		int set = (int) ((eldest.getKey() >> offset) % (Math.pow(2, setIndex)));
		long tag = (eldest.getKey() >> offset) >> setIndex;
		if (predictor != null) {
			if (size > setSize) {
				// if eldest is dead it can be removed
				if (predictor.isDead(tag)) {
					predictor.evict(tag);
					return true;
				} else {
					// else check if another block is dead
					Collection<Long> c = cache[set].keySet();
					Iterator<Long> itr = c.iterator();

					while (itr.hasNext()) {
						tag = (itr.next() >> offset) >> setIndex;
						if (predictor.isDead(tag)) {
							cache[set].remove(tag);
							return false;
						}
					}

					// no dead block found => remove eldest
					tag = (eldest.getKey() >> offset) >> setIndex;
					predictor.evict(tag);
					return true;
				}
			}
		}
		return size > setSize;
	}
}
