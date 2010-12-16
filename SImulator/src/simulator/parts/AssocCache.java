/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.parts;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.prefetchers.Prefetcher;
import simulator.prefetchers.ScalablePrefetch;
import simulator.victimcaches.IDeadblockPredictor;
import simulator.victimcaches.PlainVictimCache;

/**
 *
 * @author henri
 */
public class AssocCache extends Cache {

	/**
	 * Long: cache block
	 * Boolean: TRUE => Valid
	 */
	protected LinkedHashMap<Long, BlockAttributes>[] cache;
	protected IDeadblockPredictor predictor = null;
	private ScalablePrefetch prefetcher = null;
	private PlainVictimCache victimCache = null;

	private Simulator simulator;
	private int offset;
	private int sets;
	private int ways;
	private int setIndexBits;
	private int blockSize;
	private DirectMappedCache L1Cache;
	private int adjacent_offset;

	public AssocCache(int cacheSize, final int ways, final IDeadblockPredictor predictor, ScalablePrefetch prefetcher) {
		this(cacheSize, ways);

		this.predictor = predictor;
		this.prefetcher = prefetcher;
	}

	public AssocCache(int cacheSize, int ways, PlainVictimCache vc, ScalablePrefetch pf) {
		this(cacheSize, ways);
		prefetcher = pf;
		victimCache = vc;
	}

	public AssocCache(int cacheSize, final int ways) {
		this.simulator = SimulatorApp.getApplication().getSimulator();
		this.blockSize = simulator.getBlockSize();
		this.offset = (int) (Math.log(blockSize) / Math.log(2));
		this.ways = ways;
		this.sets = (int) (cacheSize / blockSize) / ways;
		this.setIndexBits = (int) (Math.log(sets) / Math.log(2));
		this.L1Cache = new DirectMappedCache((int) (64 * Math.pow(2, 10) / blockSize), null, null, true);
		this.adjacent_offset = (int) Math.pow(2, 8);

		this.cache = new LinkedHashMap[sets];
		for (int i = 0; i < sets; i++) {
			this.cache[i] = new LinkedHashMap<Long, BlockAttributes>(ways + 1, 1, true) {

				@Override
				protected boolean removeEldestEntry(Map.Entry<Long, BlockAttributes> eldest) {
					return removeEldest(size(), ways, eldest);
				}
			};
			// Fill the cache with invalid blocks
			for (int j = 0; j < ways; j++) {
				this.cache[i].put(0L, new BlockAttributes(0, Boolean.FALSE, Boolean.FALSE));
			}
		}
	}

	/**
	 * @deprecated
	 * @param address
	 * @return
	 */
	@Override
	public boolean access(long address) {
		if (!L1Cache.access(address)) {
			long block = address >>> offset;
			int set = (int) (block % sets);

			if (cache[set].containsKey(block) && cache[set].get(block).isValid()) {
				hits++;
				return true;
			} else {
				misses++;
				cache[set].put(block, new BlockAttributes(block, true, false));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean access(long address, long programCounter) {
		// If miss in L1 cache
		if (!L1Cache.access(address)) {

			final long block = address >>> offset;
			int set = (int) (block % sets);
			int adjacent_set = (set ^ adjacent_offset) % sets;

			if (predictor != null) {
				predictor.access(block, programCounter);
			}

			if (cache[set].containsKey(block) && cache[set].get(block).isValid()) {
				// Search own set
				hits++;
				if (prefetcher != null) {
					prefetcher.actionOnHit();
					// If was prefetched, then promote it to normal block
					if (cache[set].get(block).isPrefetched()) {
						cache[set].get(block).setPrefetched(false);
					}
				}
				return true;
			} else if (predictor != null && cache[adjacent_set].containsKey(block) && cache[adjacent_set].get(block).isValid()) {
				// Not found in own set, search adjacent set
				hits++;
				if (predictor.isDead(block)) {
					// bring back to original set
					cache[adjacent_set].remove(block);
					cache[set].put(block, new BlockAttributes(block, true, false));
				}
				if (prefetcher != null) {
					prefetcher.actionOnHit();
					// If was prefetched, it is already promoted to normal block
				}
				return true;
			} else {
				// Missed it
				if (victimCache != null && victimCache.contains(block)) {
				}

				if (victimCache != null && victimCache.contains(block)) {
					hits++;
					victimCache.remove(block);
				} else {
					misses++;
				}
				cache[set].put(block, new BlockAttributes(block, true, false));
				if (prefetcher != null) {
					prefetcher.prefetchMemory(cache, block, programCounter, sets, adjacent_offset, predictor);
					prefetcher.actionOnMiss();
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * If there is a predictor for dead blocks this method will check if there are dead blocks to remove
	 * Heb online gelezen dat als je verandering doet in de collectie je dan sowieso geen true niemeer mag teruggeven
	 * dat de uitkomst onvoorspelbaar is. Dus heb dat weggedaan en % begon logischer te worden
	 * @param size
	 * @param eldest
	 * @return
	 */
	private boolean removeEldest(int size, int ways, Map.Entry<Long, BlockAttributes> eldest) {
		// If valid block, but not prefetched without any reference
		if (predictor != null && eldest.getValue().isValid() && !eldest.getValue().isPrefetched()) {

			int set = (int) (eldest.getKey() % sets);
			int adjacent_set = (set ^ adjacent_offset) % sets;
			long eldestBlock = eldest.getKey();

			// If it is in its own set, if not in adjacent, it can be removed
			if (cache[set].containsKey(eldestBlock) && size > ways) {
				// if eldest is dead it can be removed
				if (predictor.isDead(eldestBlock)) {
					predictor.evict(eldestBlock);
					cache[set].remove(eldestBlock);
					if (victimCache != null)
						victimCache.add(eldestBlock);
					return false;
				} else {
					Collection<BlockAttributes> blocks = cache[set].values();

					// First search in own set for prefetched and dead blocks

					if (prefetcher != null) {
						// Search for prefetched blocks
						for (BlockAttributes cacheBlock : blocks) {
							if (cacheBlock.isPrefetched()) {
								predictor.evict(cacheBlock.getBlock());
								cache[set].remove(cacheBlock.getBlock());
								if (victimCache != null)
									victimCache.add(cacheBlock.getBlock());
								return false;
							}
						}
					}


					// else check if another block is dead
					for (BlockAttributes cacheBlock : blocks) {
						if (predictor.isDead(cacheBlock.getBlock())) {
							predictor.evict(cacheBlock.getBlock());
							cache[set].remove(cacheBlock.getBlock());
							if (victimCache != null)
								victimCache.add(cacheBlock.getBlock());
							return false;
						}
					}

					// Now check in adjacent set for prefetched or dead blocks

					blocks = cache[adjacent_set].values();

					if (prefetcher != null) {
						// Search for prefetched blocks
						for (BlockAttributes cacheBlock : blocks) {
							if (cacheBlock.isPrefetched()) {
								predictor.evict(cacheBlock.getBlock());
								cache[set].remove(cacheBlock.getBlock());
								if (victimCache != null)
									victimCache.add(cacheBlock.getBlock());
								return false;
							}
						}
					}

					// Search adjacent set for dead blocks
					for (BlockAttributes cacheBlock : blocks) {
						if (predictor.isDead(cacheBlock.getBlock())) {
							predictor.evict(cacheBlock.getBlock());
							// First make room in adjacent set (to avoid pingpong effect)
							cache[adjacent_set].remove(cacheBlock.getBlock());
							// Add the evicted block into adjacent set
							cache[adjacent_set].put(eldestBlock, new BlockAttributes(eldestBlock, true, false));
							// Allow the eldest to be removed from the first set (it is now
							// in the adjacent set)
							cache[set].remove(eldestBlock);
							if (victimCache != null)
								victimCache.add(eldestBlock);
							return false;
						}
					}

					// If no block is choosen the LRU element will be deleted from
					// the adjacent_set
					cache[adjacent_set].put(eldestBlock, new BlockAttributes(eldestBlock, true, false));
					if (size > ways) {
						cache[set].remove(eldestBlock);
						if (victimCache != null)
							victimCache.add(eldestBlock);
					}
					return false;
				}


				// If valid block and prefetched without any reference
			} else if (eldest.getValue().isValid() && eldest.getValue().isPrefetched()) {
				if (size > ways) {
					cache[adjacent_set].remove(eldestBlock);
					if (victimCache != null)
						victimCache.add(eldestBlock);
				}
				return false;
			} else {
				// If the block is in it's adjacent set (i.e. it is already
				// a victim block) then don't put it back into it's own set
				// but just remove it
				predictor.evict(eldestBlock);
				if (size > ways) {
					cache[adjacent_set].remove(eldestBlock);
					if (victimCache != null)
						victimCache.add(eldestBlock);
				}
				return false;
			}
		}
		if (victimCache != null)
			victimCache.add(eldest.getKey());
		return size > ways;

	}

	@Override
	public String toString() {
		String out = "Assoc";

		if (predictor != null) {
			out += "_" + predictor;
		}
		if (prefetcher != null) {
			out += "_" + prefetcher;
		}
		return out;
	}

	public LinkedHashMap<Long, BlockAttributes>[] getCache() {
		return cache;


	}

	public int getOffset() {
		return offset;


	}

	public IDeadblockPredictor getPredictor() {
		return predictor;


	}

	public int getSetIndexBits() {
		return setIndexBits;


	}

	public int getSets() {
		return sets;

	}
}
