package com.epicdima.theatraxity.domain.dao;

import com.epicdima.theatraxity.domain.models.theatre.Presentation;

import java.util.Date;

/**
 * @author EpicDima
 */
public interface PresentationDao extends BaseDao<Presentation> {
    Presentation selectByDate(Date date);
}
