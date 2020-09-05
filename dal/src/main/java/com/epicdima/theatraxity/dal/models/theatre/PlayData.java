package com.epicdima.theatraxity.dal.models.theatre;

import com.epicdima.lib.dal.annotations.Column;
import com.epicdima.lib.dal.annotations.ForeignKey;
import com.epicdima.lib.dal.annotations.PrimaryKey;
import com.epicdima.lib.dal.annotations.Table;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.getters.GetById;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.theatre.Play;

/**
 * @author EpicDima
 */
@Table(name = "plays")
public final class PlayData {

    @PrimaryKey
    @Column
    public int id;

    @Column
    public String name;

    @Column(length = 4096)
    public String description;

    @Column(name = "author_id")
    @ForeignKey(entity = AuthorData.class, parentColumn = "id")
    public int authorId;

    @Column(name = "genre_id")
    @ForeignKey(entity = GenreData.class, parentColumn = "id")
    public int genreId;

    @Column
    public boolean deleted;

    public PlayData() {}

    public PlayData(int id, String name, String description, int authorId, int genreId, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.authorId = authorId;
        this.genreId = genreId;
        this.deleted = deleted;
    }

    @Named("dal_to_play")
    @Singleton
    public static final class ToPlay implements Mapper<PlayData, Play> {

        private final GetById<Author> authorGetter;
        private final GetById<Genre> genreGetter;

        @Inject
        public ToPlay(@Named("author") GetById<Author> authorGetter,
                      @Named("genre") GetById<Genre> genreGetter) {
            this.authorGetter = authorGetter;
            this.genreGetter = genreGetter;
        }

        @Override
        public Play map(PlayData item) {
            return new Play(item.id, item.name, item.description,
                    authorGetter.getById(item.authorId),
                    genreGetter.getById(item.genreId), item.deleted);
        }
    }

    @Named("dal_from_play")
    @Singleton
    public static final class FromPlay implements Mapper<Play, PlayData> {
        @Override
        public PlayData map(Play item) {
            return new PlayData(item.getId(), item.getName(), item.getDescription(),
                    item.getAuthor().getId(), item.getGenre().getId(), item.isDeleted());
        }
    }
}
