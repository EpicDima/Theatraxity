package com.epicdima.theatraxity.domain.common;

/**
 * @author EpicDima
 */
@FunctionalInterface
public interface Mapper<T1, T2> {
    T2 map(T1 item);
}
