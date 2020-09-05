package com.epicdima.theatraxity.domain.common;

/**
 * @author EpicDima
 */
public interface Cache<K, V> {
    V get(K key);
    void set(K key, V value);
    void invalidate(K key);
    void invalidateAll();
}
