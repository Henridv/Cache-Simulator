/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simulator.GUI;

import java.util.ArrayList;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import simulator.Simulator.CacheType;

/**
 *
 * @author ruben
 */
public class CacheTypeModel implements ComboBoxModel{
    
    private ArrayList<CacheType> cacheTypes;
    private CacheType current;

    public CacheTypeModel() {
        cacheTypes = new ArrayList<CacheType>();
        cacheTypes.add(CacheType.DirectMappedCache);
    }

    public void setSelectedItem(Object o) {
        current = (CacheType) o;
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
