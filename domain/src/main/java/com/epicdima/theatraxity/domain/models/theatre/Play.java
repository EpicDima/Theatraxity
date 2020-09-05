package com.epicdima.theatraxity.domain.models.theatre;

import com.epicdima.lib.builder.annotation.Builder;

import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class Play {
    private final int id;
    private final String name;
    private final String description;
    private final Author author;
    private final Genre genre;
    private final boolean deleted;

    public Play(int id, String name, String description, Author author, Genre genre, boolean deleted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.genre = genre;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Author getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Play play = (Play) o;
        return id == play.id &&
                deleted == play.deleted &&
                Objects.equals(name, play.name) &&
                Objects.equals(description, play.description) &&
                Objects.equals(author, play.author) &&
                Objects.equals(genre, play.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, author, genre, deleted);
    }
}
