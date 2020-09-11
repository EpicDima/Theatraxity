package com.epicdima.theatraxity.dal.mysql.dao.theatre;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.PresentationDao;
import com.epicdima.theatraxity.dal.models.theatre.PresentationData;
import com.epicdima.theatraxity.domain.models.theatre.Presentation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlPresentationDao extends MySqlBaseDao<PresentationData, Presentation> implements PresentationDao {

    private static final SimpleDateFormat DATE_CONVERTER = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Inject
    public MySqlPresentationDao(ConnectionPool connectionPool,
                                @Named("dal_to_presentation") Mapper<PresentationData, Presentation> to,
                                @Named("dal_from_presentation") Mapper<Presentation, PresentationData> from) {
        super(connectionPool, to, from);
    }

    @Override
    public Presentation selectByDate(Date date) {
        return selectSingleWhere("date", "deleted",
                DATE_CONVERTER.format(date), false);
    }
}
