package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.domain.models.theatre.Play;

/**
 * @author EpicDima
 */
@Named("play")
@Singleton
public final class GetPlayById implements GetById<Play> {

    private final PlayDao playDao;

    @Inject
    public GetPlayById(PlayDao playDao) {
        this.playDao = playDao;
    }

    @Override
    public synchronized Play getById(int id) {
        return playDao.select(id);
    }
}
