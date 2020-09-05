package com.epicdima.theatraxity.dal.models.theatre;

import com.epicdima.lib.dal.annotations.Column;
import com.epicdima.lib.dal.annotations.PrimaryKey;
import com.epicdima.lib.dal.annotations.Table;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.domain.models.theatre.Genre;

/**
 * @author EpicDima
 */
@Table(name = "genres")
public final class GenreData {

    @PrimaryKey
    @Column
    public int id;

    @Column
    public String name;

    @Column
    public boolean deleted;

    public GenreData() {}

    public GenreData(int id, String name, boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }

    @Named("dal_to_genre")
    @Singleton
    public static final class ToGenre implements Mapper<GenreData, Genre> {
        @Override
        public Genre map(GenreData item) {
            return new Genre(item.id, item.name, item.deleted);
        }
    }

    @Named("dal_from_genre")
    @Singleton
    public static final class FromGenre implements Mapper<Genre, GenreData> {
        @Override
        public GenreData map(Genre item) {
            return new GenreData(item.getId(), item.getName(), item.isDeleted());
        }
    }
}
