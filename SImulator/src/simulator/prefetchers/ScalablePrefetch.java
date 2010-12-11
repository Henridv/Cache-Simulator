package simulator.prefetchers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import simulator.Simulator;
import simulator.SimulatorApp;
import simulator.parts.BlockAttributes;
import simulator.victimcaches.IDeadblockPredictor;

/**
 *
 * @author Ruben Verhack
 */
public class ScalablePrefetch extends LinearPrefetch {

    /**
     * 
     */
    public ScalablePrefetch() {
        super(1);
    }

    /**
     *
     */
    @Override
    public void actionOnHit() {
        setNumberOfBlocks(getNumberOfBlocks() + 1);
    }

    /**
     *
     */
    @Override
    public void actionOnMiss() {
        setNumberOfBlocks(1);
    }

    @Override
    public String toString() {
        return "ScalablePrefetch";
    }

    public void prefetchMemory(LinkedHashMap<Long, BlockAttributes>[] cache, long block, long programCounter, int sets, int adjacent_offset, IDeadblockPredictor predictor) {
        Simulator simulator = SimulatorApp.getApplication().getSimulator();

        Set s;
        long index;
        int set;
        int adjacent_set;
        Iterator<Entry<Long, BlockAttributes>> mapIterator;
        Entry<Long, BlockAttributes> entry;
        boolean prefetched = false;

        if (predictor != null) {
            // Only prefetch if dead/invalid blocks are available
            for (int i = 1; i < numberOfBlocks; i++) {
                prefetched = false;

                index = (long) (((block) + i) % simulator.getCacheAddresses());
                set = (int) (index % sets);
                adjacent_set = set ^ adjacent_offset;
                s = cache[set].entrySet();
                mapIterator = s.iterator();
                
                // Only prefetch if not already in cache
                if (!cache[set].containsKey(index) || !cache[adjacent_set].containsKey(index)) {

                    // Own set
                    while (mapIterator.hasNext()) {
                        entry = mapIterator.next();
                        if (predictor.isDead(entry.getKey()) || !entry.getValue().isValid()) {
                            cache[set].put(index, new BlockAttributes(index, true, true));
                            prefetched = true;
                            break;
                        }
                    }

                    // Adjacent set
                    if (!prefetched) {
                        while (mapIterator.hasNext()) {
                            entry = mapIterator.next();
                            if (predictor.isDead(entry.getKey()) || !entry.getValue().isValid()) {
                                cache[adjacent_set].put(index, new BlockAttributes(index, true, true));
                                prefetched = true;
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            for (int i = 1; i < numberOfBlocks; i++) {

                index = (long) (((block) + i) % simulator.getCacheAddresses());
                set = (int) (index % sets);
                cache[set].put(index, new BlockAttributes(index, true, true));

            }

        }
    }
}
