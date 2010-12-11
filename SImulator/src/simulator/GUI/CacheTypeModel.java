package simulator.GUI;

import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import simulator.Simulator.CacheType;
import simulator.SimulatorApp;

/**
 *
 * @author Ruben Verhack
 */
public class CacheTypeModel implements ComboBoxModel{
    
    private ArrayList<CacheType> cacheTypes;
    private CacheType current;

    /**
     *
     */
    public CacheTypeModel() {
        cacheTypes = new ArrayList<CacheType>();
        cacheTypes.add(CacheType.Plain);
        cacheTypes.add(CacheType.LinearPrefetch);
        cacheTypes.add(CacheType.ScalablePrefetch);
        cacheTypes.add(CacheType.LinearPrefetch_PlainVictimCache);
        cacheTypes.add(CacheType.ScalablePrefetch_PlainVictimCache);
        cacheTypes.add(CacheType.Assoc);
        cacheTypes.add(CacheType.AssocCounter);
        cacheTypes.add(CacheType.AssocCounter_ScalablePrefetch);
        current = cacheTypes.get(0);
    }

    /**
     *
     * @param o
     */
    public void setSelectedItem(Object o) {
        current = (CacheType) o;
        SimulatorApp.getApplication().getSimulator().setCacheType(current);
    }

    /**
     *
     * @return
     */
    public Object getSelectedItem() {
        return current;
    }

    /**
     * 
     * @return
     */
    public int getSize() {
        return cacheTypes.size();
    }

    /**
     *
     * @param i
     * @return
     */
    public Object getElementAt(int i) {
        return cacheTypes.get(i);
    }

    /**
     *
     * @param ll
     */
    public void addListDataListener(ListDataListener ll) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param ll
     */
    public void removeListDataListener(ListDataListener ll) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
