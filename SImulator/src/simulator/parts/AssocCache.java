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
	private int sets;
	private int setIndexBits;
	private int blockSize;

	public AssocCache(int cacheSize, final int ways, final IDeadblockPredictor predictor) {
		this.simulator = SimulatorApp.getApplication().getSimulator();
		this.blockSize = simulator.getBlockSize();
		this.offset = (int) (Math.log(blockSize) / Math.log(2));
		this.sets = (int) (cacheSize / blockSize) / ways;
		this.setIndexBits = (int) (Math.log(sets) / Math.log(2));

		this.predictor = predictor;

		// init sets, automatic LRU
		this.cache = new LinkedHashMap[sets];
		for (int i = 0; i < sets; i++) {
			this.cache[i] = new LinkedHashMap<Long, Boolean>(ways, 1, true) {

				@Override
				protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
					return removeEldest(size(), ways, eldest);
				}
			};
			for (int j = 0; j < ways; j++) {
				this.cache[i].put(0L, Boolean.FALSE);
			}
		}

	}

	@Override
	public boolean access(long address) {
		int set = (int) ((address >>> offset) % sets);
		long tag = (address >>> offset) >>> setIndexBits;

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
		int set = (int) ((eldest.getKey() >>> offset) % sets);
		long tag = (eldest.getKey() >>> offset) >>> setIndexBits;
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
						tag = (itr.next() >>> offset) >>> setIndexBits;
						if (predictor.isDead(tag)) {
							cache[set].remove(tag);
							return false;
						}
					}

					// no dead block found => remove eldest
					tag = (eldest.getKey() >>> offset) >>> setIndexBits;
					predictor.evict(tag);
					return true;
				}
			}
		}
		return size > setSize;
	}

	@Override
	public String toString() {
		return "Associative";
	}
}
