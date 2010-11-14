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

    public CacheTypeModel() {
        cacheTypes = new ArrayList<CacheType>();
        cacheTypes.add(CacheType.DirectMappedCache);
        current = cacheTypes.get(0);
    }

    public void setSelectedItem(Object o) {
        current = (CacheType) o;
        SimulatorApp.getApplication().getSimulator().setCacheType(current);
    }

    public Object getSelectedItem() {
        return current;
    }

    public int getSize() {
        return cacheTypes.size();
    }

    public Object getElementAt(int i) {
        return cacheTypes.get(i);
    }

    public void addListDataListener(ListDataListener ll) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeListDataListener(ListDataListener ll) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

}
