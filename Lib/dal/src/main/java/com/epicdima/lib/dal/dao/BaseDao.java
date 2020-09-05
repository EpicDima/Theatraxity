package com.epicdima.lib.dal.dao;

import java.util.List;

/**
 * @author EpicDima
 */
public interface BaseDao<T> {
    List<T> select();
    <K> T select(K key);
    T insert(T item);
    Boolean update(T item);
    Boolean delete(T item);
}
