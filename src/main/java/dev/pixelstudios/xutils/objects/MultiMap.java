package dev.pixelstudios.xutils.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MultiMap<K, V> implements Map<K, List<V>>, Iterable<Pair<K, V>> {

    private final Map<K, List<V>> map = new HashMap<>();

    public void add(K key, V value) {
        computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    public void addAll(K key, Collection<V> values) {
        computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    public List<V> getOrEmpty(K key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    public void forEach(K key, Consumer<V> action) {
        for (V value : getOrEmpty(key)) {
            action.accept(value);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public List<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public List<V> put(K key, List<V> value) {
        return map.put(key, value);
    }

    @Override
    public List<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return map.entrySet().stream()
                .flatMap(entry -> {
                    K key = entry.getKey();

                    return entry.getValue().stream()
                            .map(value -> new Pair<>(key, value));
                })
                .iterator();
    }

}
