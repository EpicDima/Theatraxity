package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.domain.models.theatre.Genre;

/**
 * @author EpicDima
 */
@Named("genre")
@Singleton
public final class GetGenreById implements GetById<Genre> {

    private final GenreDao genreDao;

    @Inject
    public GetGenreById(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public synchronized Genre getById(int id) {
        return genreDao.select(id);
    }
}
