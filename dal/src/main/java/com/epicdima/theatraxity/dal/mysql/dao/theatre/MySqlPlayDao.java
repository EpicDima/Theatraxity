package com.epicdima.theatraxity.dal.mysql.dao.theatre;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.PlayDao;
import com.epicdima.theatraxity.dal.models.theatre.PlayData;
import com.epicdima.theatraxity.domain.models.theatre.Play;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlPlayDao extends MySqlBaseDao<PlayData, Play> implements PlayDao {
    @Inject
    public MySqlPlayDao(ConnectionPool connectionPool,
                        @Named("dal_to_play") Mapper<PlayData, Play> to,
                        @Named("dal_from_play") Mapper<Play, PlayData> from) {
        super(connectionPool, to, from);
    }
}
