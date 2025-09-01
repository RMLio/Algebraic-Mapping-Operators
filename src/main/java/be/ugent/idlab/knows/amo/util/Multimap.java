package be.ugent.idlab.knows.amo.util;

import java.util.*;

public class Multimap<K, V> {

    private final Map<K, Collection<V>> map;

    public Multimap() {
        map = new HashMap<>();
    }

    public void put(K key, V value) {
        Collection<V> values;
        if (map.containsKey(key)) {
            values = map.get(key);
        } else {
            values = new ArrayList<>();
        }
        values.add(value);
        map.put(key, values);
    }

    public Collection<V> get(K key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public Collection<Map.Entry<K, V>> entries() {
        List<Map.Entry<K, V>> entries = new ArrayList<>();
        for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
            K key = entry.getKey();
            Collection<V> values = entry.getValue();
            for (V value : values) {
                entries.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        }
        return entries;
    }

    public Map<K, Collection<V>> asMap() {
        return map;
    }

    public void removeAll(K key) {
        map.remove(key);
    }

    public void replaceValues(K key, Collection<V> values) {
        map.put(key, values);
    }

    public Set<K> keys() {
        return map.keySet();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
