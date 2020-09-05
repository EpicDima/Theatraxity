package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.theatre.Author;
import com.epicdima.theatraxity.domain.models.theatre.Genre;

/**
 * @author EpicDima
 */
@Builder
public final class GenreDto {
    public Integer id;
    public String name;
    public Boolean deleted;

    public GenreDto() {}

    public GenreDto(Integer id, String name, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }

    public static GenreDto from(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName(), genre.isDeleted());
    }

    public static Genre to(GenreDto genreDto) {
        Genre.Builder builder = Genre.builder();
        if (genreDto == null) {
            return builder.build();
        }
        if (genreDto.id != null) {
            builder.id(genreDto.id);
        }
        if (genreDto.name != null) {
            builder.name(genreDto.name);
        }
        if (genreDto.deleted != null) {
            builder.deleted(genreDto.deleted);
        }
        return builder.build();
    }
}
