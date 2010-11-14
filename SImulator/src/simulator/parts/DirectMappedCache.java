package simulator.parts;

import java.util.ArrayList;
import simulator.Simulator;
import simulator.SimulatorApp;

/**
 *
 * @author Ruben Verhack
 */
public class DirectMappedCache extends Cache {

    private int[] cache;
    private Simulator simulator;
    protected ArrayList<Integer> onItsWayList;

    public DirectMappedCache() {

        cache = new int[Simulator.CACHE_SIZE / Simulator.WORD_SIZE];
        onItsWayList = new ArrayList<Integer>();
    }

    @Override
    public boolean access(final int address) {

        simulator = SimulatorApp.getApplication().getSimulator();
        boolean hit;
        int cacheAddress = (address / Simulator.WORD_SIZE) % Simulator.CACHE_SIZE;

        if (cache[cacheAddress] == address || onItsWayList.contains(address)) {
            hit = true;
            hits++;
        } else {
            final long time = simulator.getClock();
            Thread delayThread = new Thread(new Runnable() {

                public void run() {
                    onItsWayList.add(address);
                    while (simulator.getClock() < time + Simulator.MEM_ACCESS_TIME) {
                    }
                    cache[(int) (address / Simulator.CACHE_SIZE)] = address;
                    onItsWayList.remove((Integer) address);
                }
            });
            hit = false;
            misses++;
        }

        return hit;
    }
}
