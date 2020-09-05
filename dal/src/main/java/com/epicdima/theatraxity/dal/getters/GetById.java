package com.epicdima.theatraxity.dal.getters;

/**
 * @author EpicDima
 */
@FunctionalInterface
public interface GetById<T> {
    T getById(int id);
}
