package cdb.common.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Chao Chen
 * @version $Id: ListInMap.java, v 0.1 Nov 24, 2015 5:12:35 PM chench Exp $
 */
public class ListInMap<K, V> {
    /** inner data structure*/
    private HashMap<K, List<V>> mapInner;

    /**
     * construction
     */
    public ListInMap() {
        mapInner = new HashMap<K, List<V>>();
    }

    //-----------------------------------------------------------------------
    /**
     * Puts a key-value mapping into this map.
     * 
     * @param key  the key to add
     * @param value  the value to add
     */
    public void put(K key, V value) {
        List<V> valArr = mapInner.get(key);
        if (valArr == null) {
            valArr = new ArrayList<V>();
            mapInner.put(key, valArr);
        }
        valArr.add(value);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the key specified.
     * 
     * @param key  the key
     * @return the mapped value, null if no match
     */
    public List<V> get(K key) {
        return mapInner.get(key);
    }

    /**
     * Checks whether the map is currently empty.
     * 
     * @return true if the map is currently size zero
     */
    public boolean isEmpty() {
        return mapInner.isEmpty();
    }

    /**
     * <p>The set is created the first time this method is called,
     * and returned in response to all subsequent calls.  No synchronization
     * is performed, so there is a slight chance that multiple calls to this
     * method will not all return the same set.
     */
    public Set<K> keySet() {
        return mapInner.keySet();
    }

    /**
     *
     * <p>The collection is created the first time this method is called, and
     * returned in response to all subsequent calls.  No synchronization is
     * performed, so there is a slight chance that multiple calls to this
     * method will not all return the same collection.
     */
    public Collection<List<V>> values() {
        return mapInner.values();
    }
}
