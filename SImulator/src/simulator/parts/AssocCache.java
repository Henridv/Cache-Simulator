/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.parts;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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
		long block = address >>> offset;
		int set = (int) (block % sets);

		if (cache[set].containsKey(block) && cache[set].get(block)) {
			hits++;
			return true;
		} else {
			misses++;
			cache[set].put(block, Boolean.TRUE);
			return false;
		}
	}

    @Override
    public boolean access(long address, long programCounter) {
		long block = address >>> offset;
		int set = (int) (block % sets);

		if (predictor != null) {
			predictor.access(block, programCounter);
		}
		if (cache[set].containsKey(block) && cache[set].get(block)) {
			hits++;
			return true;
		} else {
			misses++;
			cache[set].put(block, Boolean.TRUE);
			return false;
		}
    }
	/**
	 * If there is a predictor for dead blocks this method will check if there are dead blocks to remove
	 * @param size
	 * @param eldest
	 * @return
	 */
	private boolean removeEldest(int size, int ways, Map.Entry<Long, Boolean> eldest) {
		if (predictor != null) {
			int set = (int) (eldest.getKey() % sets);
			long eldestBlock = eldest.getKey();

			if (size > ways) {
				// if eldest is dead it can be removed
				if (predictor.isDead(eldestBlock)) {
					predictor.evict(eldestBlock);
					return true;
				} else {
					// else check if another block is dead
					Set<Long> blocks = cache[set].keySet();

					for (Long cacheBlock : blocks) {
						if (predictor.isDead(cacheBlock)) {
							predictor.evict(cacheBlock);
							cache[set].remove(cacheBlock);
							return false;
						}
					}

					predictor.evict(eldestBlock);
					return true;
				}
			}
		}
		return size > ways;
	}

	@Override
	public String toString() {
		String out = "Associative";
		if (predictor != null)
			out += " + predictor";

		return out;
	}
}
