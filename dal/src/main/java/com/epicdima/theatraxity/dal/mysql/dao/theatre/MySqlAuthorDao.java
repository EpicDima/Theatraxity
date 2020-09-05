package com.epicdima.theatraxity.dal.mysql.dao.theatre;

import com.epicdima.lib.dal.other.ConnectionPool;
import com.epicdima.lib.di.annotations.Inject;
import com.epicdima.lib.di.annotations.Named;
import com.epicdima.lib.di.annotations.Singleton;
import com.epicdima.theatraxity.domain.common.Mapper;
import com.epicdima.theatraxity.dal.mysql.dao.MySqlBaseDao;
import com.epicdima.theatraxity.domain.dao.AuthorDao;
import com.epicdima.theatraxity.dal.models.theatre.AuthorData;
import com.epicdima.theatraxity.domain.models.theatre.Author;

/**
 * @author EpicDima
 */
@Singleton
public final class MySqlAuthorDao extends MySqlBaseDao<AuthorData, Author> implements AuthorDao {
    @Inject
    public MySqlAuthorDao(ConnectionPool connectionPool,
                          @Named("dal_to_author") Mapper<AuthorData, Author> to,
                          @Named("dal_from_author") Mapper<Author, AuthorData> from) {
        super(connectionPool, to, from);
    }
}
