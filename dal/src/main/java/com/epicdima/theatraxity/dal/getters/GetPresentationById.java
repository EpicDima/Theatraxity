package com.epicdima.theatraxity.dal.getters;

import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;

/**
 * @author EpicDima
 */
@Named("presentation")
@Singleton
public final class GetPresentationById implements GetById<Presentation> {

    private final PresentationDao presentationDao;

    @Inject
    public GetPresentationById(PresentationDao presentationDao) {
        this.presentationDao = presentationDao;
    }

    @Override
    public synchronized Presentation getById(int id) {
        return presentationDao.select(id);
    }
}
