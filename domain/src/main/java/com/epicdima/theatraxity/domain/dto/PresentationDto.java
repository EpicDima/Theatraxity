package com.epicdima.theatraxity.domain.dto;

import com.epicdima.lib.builder.annotation.Builder;
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;

import java.util.Date;

/**
 * @author EpicDima
 */
@Builder
public final class PresentationDto {
    public Integer id;
    public Date date;
    public PlayDto play;
    public Boolean deleted;

    public PresentationDto() {}

    public PresentationDto(Integer id, Date date, PlayDto play, Boolean deleted) {
        this.id = id;
        this.date = date;
        this.play = play;
        this.deleted = deleted;
    }

    public static PresentationDto from(Presentation presentation) {
        return new PresentationDto(presentation.getId(), presentation.getDate(),
                PlayDto.from(presentation.getPlay()), presentation.isDeleted());
    }

    public static Presentation to(PresentationDto presentationDto) {
        Presentation.Builder builder = Presentation.builder();
        if (presentationDto == null) {
            return builder.build();
        }
        if (presentationDto.id != null) {
            builder.id(presentationDto.id);
        }
        if (presentationDto.date != null) {
            builder.date(presentationDto.date);
        }
        if (presentationDto.play != null) {
            builder.play(PlayDto.to(presentationDto.play));
        }
        if (presentationDto.deleted != null) {
            builder.deleted(presentationDto.deleted);
        }
        return builder.build();
    }
}
