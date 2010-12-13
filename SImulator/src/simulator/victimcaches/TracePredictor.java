/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.victimcaches;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author henri
 */
public class TracePredictor implements IDeadblockPredictor {

	protected HashMap<Long, Long> signature;
	protected HashMap<Long, Long> counter;
	protected ArrayList<Long> deadBlocks;

	public TracePredictor() {
		signature = new HashMap<Long, Long>();
		counter = new HashMap<Long, Long>();
		deadBlocks = new ArrayList<Long>();
	}

	public boolean access(long cacheBlock, long programCounter) {
		Long trace = signature.get(cacheBlock);
		if (trace == null) {
			trace = counter.get(cacheBlock);
			if (trace == null) {
				counter.put(cacheBlock, programCounter);
			} else {
				counter.put(cacheBlock, trace + programCounter);
			}
			return false;
		} else {
			if (trace.equals(counter.get(cacheBlock))) {
				if (!deadBlocks.contains(cacheBlock)) {
					deadBlocks.add(cacheBlock);
				}
				return true;
			} else {
				trace = counter.get(cacheBlock) + programCounter;
				counter.put(cacheBlock, trace);
				return false;
			}
		}
	}

	public void evict(long cacheBlock) {
		if (signature.get(cacheBlock) == null) {
			signature.put(cacheBlock, counter.get(cacheBlock));
			counter.put(cacheBlock, 0L);
			deadBlocks.remove(cacheBlock);
		}
	}

	public boolean isDead(long cacheBlock) {
		return deadBlocks.contains(cacheBlock);
	}

	@Override
	public String toString() {
		return "Trace";
	}
}
