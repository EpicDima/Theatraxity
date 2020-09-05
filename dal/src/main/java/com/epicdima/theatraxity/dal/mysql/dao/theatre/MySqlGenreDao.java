package com.epicdima.theatraxity.dal.mysql.dao.theatre;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.GenreDao;
import com.epicdima.theatraxity.dal.models.theatre.GenreData;
import com.epicdima.theatraxity.domain.models.theatre.Genre;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlGenreDao extends MySqlBaseDao<GenreData, Genre> implements GenreDao {
    @Inject
    public MySqlGenreDao(ConnectionPool connectionPool,
                         @Named("dal_to_genre") Mapper<GenreData, Genre> to,
                         @Named("dal_from_genre") Mapper<Genre, GenreData> from) {
        super(connectionPool, to, from);
    }

    @Override
    public Genre selectByName(String name) {
        return selectSingleWhere("name", "deleted", name, false);
    }
}
