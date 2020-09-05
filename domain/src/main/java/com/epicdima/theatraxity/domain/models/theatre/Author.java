package com.epicdima.theatraxity.domain.models.theatre;

import com.epicdima.lib.builder.annotation.Builder;

import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class Author {
    private final int id;
    private final String name;
    private final boolean deleted;

    public Author(int id, String name, boolean deleted) {
        this.id = id;
        this.name = name;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id == author.id &&
                deleted == author.deleted &&
                Objects.equals(name, author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deleted);
    }
}
