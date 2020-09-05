package com.epicdima.theatraxity.domain.models.theatre;

import com.epicdima.lib.builder.annotation.Builder;

import java.util.Date;
import java.util.Objects;

/**
 * @author EpicDima
 */
@Builder
public final class Presentation {
    private final int id;
    private final Date date;
    private final Play play;
    private final boolean deleted;

    public Presentation(int id, Date date, Play play, boolean deleted) {
        this.id = id;
        this.date = date;
        this.play = play;
        this.deleted = deleted;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Play getPlay() {
        return play;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Presentation that = (Presentation) o;
        return id == that.id &&
                deleted == that.deleted &&
                Objects.equals(date, that.date) &&
                Objects.equals(play, that.play);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, play, deleted);
    }
}
