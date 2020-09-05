package com.epicdima.theatraxity.domain.dao;

import com.epicdima.theatraxity.domain.models.theatre.Genre;

/**
 * @author EpicDima
 */
public interface GenreDao extends BaseDao<Genre> {
    Genre selectByName(String name);
}
