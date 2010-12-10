/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.parts;

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
    private DirectMappedCache L1Cache;
    private boolean removeReceivingBlock;

    public AssocCache(int cacheSize, final int ways, final IDeadblockPredictor predictor) {
        this.simulator = SimulatorApp.getApplication().getSimulator();
        this.blockSize = simulator.getBlockSize();
        this.offset = (int) (Math.log(blockSize) / Math.log(2));
        this.sets = (int) (cacheSize / blockSize) / ways;
        this.setIndexBits = (int) (Math.log(sets) / Math.log(2));
        this.L1Cache = new DirectMappedCache((int) (64 * Math.pow(2, 10) / blockSize), null, null, true);
        this.removeReceivingBlock = false;

        this.predictor = predictor;

        // init sets, automatic LRU
        this.cache = new LinkedHashMap[sets];
        for (int i = 0; i < sets; i++) {
            this.cache[i] = new LinkedHashMap<Long, Boolean>(ways + 1, 1, true) {

                @Override
                protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
                    return removeEldest(size(), ways, eldest);
                }
            };
            // Zorgde voor nullpointerexceptions
            for (int j = 0; j < ways; j++) {
                this.cache[i].put(0L, Boolean.FALSE);
            }
        }

    }

    @Override
    public boolean access(long address) {
        if (!L1Cache.access(address)) {
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
        return true;
    }

    @Override
    public boolean access(long address, long programCounter) {
        if (!L1Cache.access(address)) {
            
            final long block = address >>> offset;
            int set = (int) (block % sets);
            int adjacent_set = (set | (int) Math.pow(2, 3)); // k=3 was choosen optimal in article

            if (predictor != null) {
                predictor.access(block, programCounter);
            }

            if (cache[set].containsKey(block) && cache[set].get(block)) {
                // Search own set
                hits++;
                return true;
            } else if (predictor != null && cache[adjacent_set].containsKey(block) && cache[adjacent_set].get(block)) {
                // Not found in own set, search adjacent set
                hits++;
                //if (predictor.isDead(block)) {
                    // bring back to original set
                    cache[adjacent_set].remove(block);
                    cache[set].put(block, Boolean.TRUE);
                //}
                return true;
            } else {
                // Missed it
                misses++;
                cache[set].put(block, Boolean.TRUE);
                return false;
            }
        }
        return true;
    }

    /**
     * If there is a predictor for dead blocks this method will check if there are dead blocks to remove
     * @param size
     * @param eldest
     * @return
     */
    private boolean removeEldest(int size, int ways, Map.Entry<Long, Boolean> eldest) {
        if (predictor != null && !removeReceivingBlock && eldest.getKey() != 0L) {
            int set = (int) (eldest.getKey() % sets);
            int adjacent_set = (set | (int) Math.pow(2, 3)); // k=3 was choosen optimal in article

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

                    // Search adjacent set for dead blocks
                    blocks = cache[adjacent_set].keySet();
                    for (Long cacheBlock : blocks) {
                        if (predictor.isDead(cacheBlock)) {
                            predictor.evict(cacheBlock);
                            // First make room in adjacent set (to avoid pingpong effect)
                            cache[adjacent_set].remove(cacheBlock);
                            // Add the evicted block into adjacent set
                            cache[adjacent_set].put(eldest.getKey(), eldest.getValue());
                            // Allow the eldest to be removed from the first set (it is now
                            // in the adjacent set)
                            return true;
                        }
                    }

                    // Remove the LRU element in the adjacent set, set this to
                    // true to avoid the pingpong effect and add the evicted block
                    // as MRU element into the adjacent set
                    removeReceivingBlock = true;
                    cache[adjacent_set].put(eldest.getKey(), eldest.getValue());


                    //predictor.evict(eldestBlock);
                    return true;


                }
            }
        } else if (removeReceivingBlock && eldest.getKey() != 0L) {
            // Remove block in adjacent set, don't put it in it's adjacent set
            // to avoid pingpong
            removeReceivingBlock = false;
            if (size > ways) {
                predictor.evict(eldest.getKey());
            }

        }
        return size > ways;

    }

    @Override
    public String toString() {
        String out = "Associative";

        if (predictor != null) {
            out += " + predictor";
        }
        return out;
    }

    public LinkedHashMap<Long, Boolean>[] getCache() {
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
