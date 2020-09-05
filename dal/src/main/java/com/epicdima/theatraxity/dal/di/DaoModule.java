package com.epicdima.theatraxity.dal.di;

import com.epicdima.lib.di.annotations.Module;
import com.epicdima.theatraxity.dal.mysql.dao.business.MySqlOrderDao;
import com.epicdima.theatraxity.dal.mysql.dao.business.MySqlTicketDao;
import com.epicdima.theatraxity.dal.mysql.dao.theatre.MySqlAuthorDao;
import com.epicdima.theatraxity.dal.mysql.dao.theatre.MySqlGenreDao;
import com.epicdima.theatraxity.dal.mysql.dao.theatre.MySqlPlayDao;
import com.epicdima.theatraxity.dal.mysql.dao.theatre.MySqlPresentationDao;
import com.epicdima.theatraxity.dal.mysql.dao.user.MySqlUserDao;

/**
 * @author EpicDima
 */
@Module(implementations = {
        MySqlUserDao.class,
        MySqlAuthorDao.class,
        MySqlGenreDao.class,
        MySqlPlayDao.class,
        MySqlPresentationDao.class,
        MySqlOrderDao.class,
        MySqlTicketDao.class
})
public final class DaoModule {
}
