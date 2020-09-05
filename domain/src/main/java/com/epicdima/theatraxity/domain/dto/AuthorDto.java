package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.theatre.Author;

/**
 * @author EpicDima
 */
@Builder
public final class AuthorDto {
    public Integer id;
    public String name;
    public Boolean deleted;

    public AuthorDto() {}

    public AuthorDto(Integer id, String name, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }

    public static AuthorDto from(Author author) {
        return new AuthorDto(author.getId(), author.getName(), author.isDeleted());
    }

    public static Author to(AuthorDto authorDto) {
        Author.Builder builder = Author.builder();
        if (authorDto == null) {
            return builder.build();
        }
        if (authorDto.id != null) {
            builder.id(authorDto.id);
        }
        if (authorDto.name != null) {
            builder.name(authorDto.name);
        }
        if (authorDto.deleted != null) {
            builder.deleted(authorDto.deleted);
        }
        return builder.build();
    }
}
