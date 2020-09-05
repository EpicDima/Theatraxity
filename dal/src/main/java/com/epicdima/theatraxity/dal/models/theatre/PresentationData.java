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
import com.epicdima.theatraxity.domain.models.theatre.Play;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;

import java.util.Date;

/**
 * @author EpicDima
 */
@Table(name = "presentations")
public final class PresentationData {

    @PrimaryKey
    @Column
    public int id;

    @Column
    public Date date;

    @Column(name = "play_id")
    @ForeignKey(entity = PlayData.class, parentColumn = "id")
    public int playId;

    @Column
    public boolean deleted;

    public PresentationData() {}

    public PresentationData(int id, Date date, int playId, boolean deleted) {
        this.id = id;
        this.date = date;
        this.playId = playId;
        this.deleted = deleted;
    }

    @Named("dal_to_presentation")
    @Singleton
    public static final class ToPresentation implements Mapper<PresentationData, Presentation> {

        private final GetById<Play> playGetter;

        @Inject
        public ToPresentation(@Named("play") GetById<Play> playGetter) {
            this.playGetter = playGetter;
        }

        @Override
        public Presentation map(PresentationData item) {
            return new Presentation(item.id, item.date, playGetter.getById(item.playId), item.deleted);
        }
    }

    @Named("dal_from_presentation")
    @Singleton
    public static final class FromPresentation implements Mapper<Presentation, PresentationData> {
        @Override
        public PresentationData map(Presentation item) {
            return new PresentationData(item.getId(), item.getDate(), item.getPlay().getId(), item.isDeleted());
        }
    }
}
