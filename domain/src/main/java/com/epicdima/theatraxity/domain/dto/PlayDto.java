package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.theatre.Genre;
import com.epicdima.theatraxity.domain.models.theatre.Play;

/**
 * @author EpicDima
 */
@Builder
public final class PlayDto {
    public Integer id;
    public String name;
    public String description;
    public AuthorDto author;
    public GenreDto genre;
    public Boolean deleted;

    public PlayDto() {}

    public PlayDto(Integer id, String name, String description,
                   AuthorDto author, GenreDto genre, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.genre = genre;
        this.deleted = deleted;
    }

    public static PlayDto from(Play play) {
        return new PlayDto(play.getId(), play.getName(), play.getDescription(),
                AuthorDto.from(play.getAuthor()), GenreDto.from(play.getGenre()),
                play.isDeleted());
    }

    public static Play to(PlayDto playDto) {
        Play.Builder builder = Play.builder();
        if (playDto == null) {
            return builder.build();
        }
        if (playDto.id != null) {
            builder.id(playDto.id);
        }
        if (playDto.name != null) {
            builder.name(playDto.name);
        }
        if (playDto.description != null) {
            builder.description(playDto.description);
        }
        if (playDto.author != null) {
            builder.author(AuthorDto.to(playDto.author));
        }
        if (playDto.genre != null) {
            builder.genre(GenreDto.to(playDto.genre));
        }
        if (playDto.deleted != null) {
            builder.deleted(playDto.deleted);
        }
        return builder.build();
    }
}
